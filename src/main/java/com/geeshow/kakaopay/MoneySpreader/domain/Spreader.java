package com.geeshow.kakaopay.MoneySpreader.domain;

import com.geeshow.kakaopay.MoneySpreader.utils.ticket.Ticket;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.TicketGenerator;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spreader extends BaseEntity {

    // 뿌리기 토큰
    @Column(unique = true, nullable = false, length = 3)
    private String token;

    // 뿌리기 총 금액
    @Column(nullable = false)
    private Long amount;

    // 뿌리기 방 번호
    @Column private String roomId;

    // 뿌리기 사용자 ID
    @Column private Long spreaderUserId;

    // 뿌리기 건수
    @Column(nullable = false)
    private Integer ticketCount;

    // 조회 만료 일시
    @Column(nullable = false)
    private LocalDateTime expireReadDate;

    // 수취 만료 일시
    @Column(nullable = false)
    private LocalDateTime expireReceiptDate;

    // 뿌리기 발행 티켓 목록
    @OneToMany(mappedBy = "spreader", cascade = CascadeType.ALL)
    @Builder.Default
    List<SpreaderTicket> spreaderTickets = new ArrayList<>();

    public void registerTickets(TicketGenerator<Ticket> ticketGenerator) {
        ticketGenerator.generate().stream()
                .forEach(ticket -> {
                    addSpreaderTicket(ticket.getAmount());
                });
    }

    private void addSpreaderTicket(long amount) {
        SpreaderTicket ticket = SpreaderTicket.builder().amount(amount).build();
        spreaderTickets.add(ticket);
        ticket.setSpreader(this);
    }

    public boolean isExpiredRead() {
        return this.expireReadDate.isBefore(LocalDateTime.now());
    }

    public boolean isExpiredReceive() {
        return this.expireReceiptDate.isBefore(LocalDateTime.now());
    }

    public boolean isReceiverAlready(long userId) {
        return spreaderTickets.stream()
                .anyMatch(ticket ->
                        Optional.ofNullable(ticket.getReceiverUserId()).orElse(0L) == userId );
    }

    public Optional<SpreaderTicket> findTicketBelongTo(long userId) {
        return spreaderTickets.stream()
                .filter(ticket -> ticket.isBelongTo(userId))
                .findFirst();
    }

    public List<SpreaderTicket> getReceivedTickets() {
        return spreaderTickets.stream()
                .filter(SpreaderTicket::isReceived)
                .collect(Collectors.toList());
    }

    public long getTotalReceiptAmount() {
        return spreaderTickets.stream()
                .filter(spreaderTicket -> Optional.ofNullable(spreaderTicket.getReceiverUserId()).orElse(0L) > 0)
                .map(SpreaderTicket::getAmount)
                .reduce(0L, Long::sum);
    }

    public Optional<SpreaderTicket> findReceivableTicket() {
        return getSpreaderTickets().stream()
                .filter(ticket -> !ticket.isReceived())
                .findFirst();
    }
}