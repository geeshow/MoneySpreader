package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import com.geeshow.kakaopay.MoneySpreader.exception.NotEnoughSpreadAmount;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class RandomTicketGenerator implements TicketGenerator<Ticket> {

    /**
     * amount 범위 내에 무작위 금액의 티켓을 생성
     *
     * @param amount 총 티켓 발생 금액
     * @param ticketCount 총 티켓 발행 건수
     * @return 티켓목록
     */
    @Override
    public ArrayList<Ticket> generate(long amount, int ticketCount) {

        long leftAmount = amount - ticketCount; // 최소 할당 금액을 뺀 후 분배 한다.
        ArrayList<Ticket> tickets = new ArrayList<>();

        // 무작위로 금액을 발생하여 리스트에 추가.
        while( tickets.size() < ticketCount ) {
            long randomAmount = getRandomAmount(leftAmount);
            leftAmount -= randomAmount;
            tickets.add(new Ticket(randomAmount + 1)); // 최소 할당금액 1원을 더함.
        }

        // 남은 금액은 마지막 티켓에 포함
        Ticket lastTicket = tickets.get(ticketCount - 1);
        lastTicket.setAmount(lastTicket.getAmount() + leftAmount);

        // 랜덤으로 생성되는 금액을 한번 더 섞어 준다.
        Collections.sort(tickets);
        return tickets;
    }

    private long getRandomAmount(long max) {
        if ( max == 0 ) return 0;
        return new Random().longs(1, 0, max + 1).sum();
    }
}
