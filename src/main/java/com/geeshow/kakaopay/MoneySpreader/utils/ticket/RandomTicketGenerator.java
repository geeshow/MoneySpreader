package com.geeshow.kakaopay.MoneySpreader.utils.ticket;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class RandomTicketGenerator implements TicketGenerator<Ticket> {

    private long amount;
    private int count;
    private int minValue;
    private long leftAmount;

    public static RandomTicketGenerator getGenerator(long amount, int count, int minValue) {
        return RandomTicketGenerator.builder()
                .amount(amount)
                .count(count)
                .minValue(minValue)
                .leftAmount(amount - (count * minValue))
                .build();
    }
    /**
     * amount 범위 내에 무작위 금액의 티켓을 생성
     *
     * @return 티켓목록
     */
    @Override
    public ArrayList<Ticket> generate() {

        ArrayList<Ticket> tickets = new ArrayList<>();

        initLeftAmount();

        // 무작위로 금액을 발생하여 리스트에 추가.
        while( tickets.size() < this.count - 1 ) {
            tickets.add(new Ticket(nextAmount()));
        }

        // 남은 금액은 마지막 티켓으로 추가
        tickets.add(new Ticket(lastAmount()));

        // 랜덤으로 생성되는 금액을 한번 더 섞어 준다.
        Collections.sort(tickets);
        return tickets;
    }


    private void initLeftAmount() {
        this.leftAmount = this.amount - (this.count * this.minValue); // 최소 할당 금액을 뺀 후 분배 한다.
    }

    /**
     minValue ~ leftAmount 범위의 랜덤값을 구한다.
     */
    private long nextAmount() {
        if ( this.leftAmount <= 0 ) return this.minValue;

        long randomAmount = new Random().longs(1, 0, this.leftAmount + 1).sum();
        this.leftAmount -= randomAmount;
        return randomAmount + this.minValue;
    }

    /**
     남은 값을 구한다.
     */
    private long lastAmount() {
        long lastAmount = this.leftAmount + this.minValue;
        this.leftAmount = 0;

        return lastAmount;
    }
}
