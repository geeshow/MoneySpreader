package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import java.util.ArrayList;

public interface TicketGenerator<T> {
    ArrayList<Ticket> generate();

    static ArrayList<Ticket> generate2() {
        return null;
    }
}
