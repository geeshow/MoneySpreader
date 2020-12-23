package com.geeshow.kakaopay.MoneySpreader.domain;

import com.geeshow.kakaopay.MoneySpreader.utils.ticket.Ticket;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.TicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.exception.NotRemainTicket;
import lombok.*;

import javax.persistence.*;
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
    @Column private String roomNumber;

    // spreader user id
    @Column private Long spreaderUserId;

    // spreading or receiver number
    @Column(nullable = false)
    private Integer ticketCount;

    // Receiver Users
    @OneToMany(mappedBy = "spreader", cascade = CascadeType.ALL)
    @Builder.Default
    List<SpreaderTicket> spreaderTickets = new ArrayList<>();

    public void registeTickets(TicketGenerator<Ticket> ticketGenerator) {
        ticketGenerator.generate(getAmount(), getTicketCount()).stream()
                .forEach(ticket -> {
                    addSpreaderTicket(ticket.getAmount());
                });
    }

    private void addSpreaderTicket(long amount) {
        SpreaderTicket ticket = SpreaderTicket.builder()
                .amount(amount)
                .received(false)
                .build();
        spreaderTickets.add(ticket);
        ticket.setSpreader(this);
    }

    public SpreaderTicket findOneNotReceive() {
        return getSpreaderTickets().stream()
                .filter(SpreaderTicket::isReceived)
                .findFirst()
                .orElseThrow(() -> new NotRemainTicket(token));
    }
}