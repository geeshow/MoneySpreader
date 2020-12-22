package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import lombok.*;

import javax.persistence.*;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Ticket implements Comparable<Ticket> {

    // 티켓 금액
    @Column(nullable = false)
    private Long amount;

    // 티켓 순서
    @Column(nullable = false)
    private Integer sortNumber;

    public Ticket(long amount) {
        this.amount = amount;
        sortNumber = new Random().nextInt();
    }

    @Override
    public int compareTo(Ticket ticket) {
        return this.sortNumber.compareTo(ticket.getSortNumber());
    }
}
