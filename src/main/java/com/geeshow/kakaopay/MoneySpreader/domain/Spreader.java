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

    // spreading or receiver number
    @Column(nullable = false)
    private Integer ticketCount;

    // Spreader User ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomuser_id")
    private RoomUser roomUser;

    // Receiver Users
    @OneToMany(mappedBy = "spreader", cascade = CascadeType.PERSIST)
    @Builder.Default
    List<SpreaderTicket> spreaderTickets = new ArrayList<>();

    public void registeTickets(TicketGenerator<Ticket> ticketGenerator) {
        ticketGenerator.generate(getAmount(), getTicketCount()).stream()
                .forEach(ticket -> {
                    addSpreaderTicket(ticket.getAmount());
                });
    }

    private void addSpreaderTicket(long amount) {
        spreaderTickets.add(
                SpreaderTicket.builder()
                        .amount(amount)
                        .build()
        );
    }

    public SpreaderTicket findOneNotReceive() {
        return getSpreaderTickets().stream()
                .filter(SpreaderTicket::isReceived)
                .findFirst()
                .orElseThrow(() -> new NotRemainTicket(token));
    }
}