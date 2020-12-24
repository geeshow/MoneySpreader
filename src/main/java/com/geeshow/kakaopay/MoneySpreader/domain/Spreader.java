package com.geeshow.kakaopay.MoneySpreader.domain;

import com.geeshow.kakaopay.MoneySpreader.utils.ticket.Ticket;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.TicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.exception.NotRemainTicketException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spreader extends BaseEntity {

    // transaction token
    @Column(unique = true, nullable = false, length = 3)
    private String token;

    // spreading total amount
    @Column(nullable = false)
    private Long amount;

    // room number
    @Column private String roomId;

    // spreader user id
    @Column private Long spreaderUserId;

    // spreading or receiver number
    @Column(nullable = false)
    private Integer ticketCount;

    @Column(nullable = false)
    private LocalDateTime expireReadDate;

    @Column(nullable = false)
    private LocalDateTime expireReceiptDate;

    // Receiver Users
    @OneToMany(mappedBy = "spreader", cascade = CascadeType.ALL)
    @Builder.Default
    List<SpreaderTicket> spreaderTickets = new ArrayList<>();

    public void registeTickets(TicketGenerator<Ticket> ticketGenerator) {
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

    public SpreaderTicket findTicketBelongTo(long userId) {
        return spreaderTickets.stream()
                .filter(ticket -> ticket.isBelongTo(userId))
                .findFirst().orElse(null);
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