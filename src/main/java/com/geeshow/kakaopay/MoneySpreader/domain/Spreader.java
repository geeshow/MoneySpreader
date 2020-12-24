package com.geeshow.kakaopay.MoneySpreader.domain;

import com.geeshow.kakaopay.MoneySpreader.utils.ticket.Ticket;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.TicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.exception.NotRemainTicketException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDateTime expiredDate;

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

    public boolean isExpired() {
        return getExpiredDate().isBefore(LocalDateTime.now());
    }

    public Long getReceiptAmount() {
        return 123L;
//        return spreaderTickets.stream()
//                .filter(spreaderTicket -> Optional.ofNullable(spreaderTicket.getReceiverUserId()).orElse(0L) > 0)
//                .map(SpreaderTicket::getAmount)
//                .reduce(0L, Long::sum);
    }

    public SpreaderTicket findOneNotReceive() {
        return getSpreaderTickets().stream()
                .filter(SpreaderTicket::isReceived)
                .findFirst()
                .orElseThrow(() -> new NotRemainTicketException(token));
    }
}