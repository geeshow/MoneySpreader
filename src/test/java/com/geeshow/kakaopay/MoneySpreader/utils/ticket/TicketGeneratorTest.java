package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TicketGeneratorTest {

    @Test
    @DisplayName("티켓 생성 테스트 - 최소단위")
    public void generateMinimumPriceTicket() throws Exception {

        // given
        RandomTicketGenerator randomTicketGenerator = new RandomTicketGenerator();
        long amount = 100;
        int ticketCount = 100;

        // when
        ArrayList<Ticket> tickets = randomTicketGenerator.generate(amount, ticketCount);

        // then
        assertTrue(tickets.size() == 100);
        tickets.stream().forEach(ticket -> {
            assertTrue(ticket.getAmount() == 1);
        });

    }

    @Test
    @DisplayName("티켓 생성 테스트 - 티켓금액 합계 일치")
    public void generateSumPriceTicket() throws Exception {

        // given
        RandomTicketGenerator randomTicketGenerator = new RandomTicketGenerator();
        long amount = 1000;
        int ticketCount = 5;

        // when
        ArrayList<Ticket> tickets = randomTicketGenerator.generate(amount, ticketCount);
        Long result = tickets.stream().collect(Collectors.summingLong(Ticket::getAmount));

        // then
        assertTrue(result.longValue() == amount);
    }
}