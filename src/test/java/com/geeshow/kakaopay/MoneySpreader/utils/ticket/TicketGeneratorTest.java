package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TicketGeneratorTest {

    @Test
    @DisplayName("티켓 생성 테스트 - 최소단위")
    public void generateMinimumPriceTicket() throws Exception {

        // given
        RandomTicketGenerator randomTicketGenerator = RandomTicketGenerator.builder()
                .amount(100)
                .count(100)
                .minValue(1)
                .build();

        // when
        ArrayList<Ticket> tickets = randomTicketGenerator.generate();

        // then
        assertThat(tickets.size()).isEqualTo(100);
        tickets.stream().forEach(ticket -> {
            assertThat(ticket.getAmount()).isEqualTo(1);
        });

    }

    @Test
    @DisplayName("티켓 생성 테스트 - 티켓금액 합계 일치")
    public void generateSumPriceTicket() throws Exception {

        // given
        long amount = 1000;
        RandomTicketGenerator randomTicketGenerator = RandomTicketGenerator.builder()
                .amount(amount)
                .count(5)
                .minValue(1)
                .build();

        // when
        ArrayList<Ticket> tickets = randomTicketGenerator.generate();
        Long result = tickets.stream().collect(Collectors.summingLong(Ticket::getAmount));

        // then
        assertThat(result.longValue()).isEqualTo(amount);
    }
}