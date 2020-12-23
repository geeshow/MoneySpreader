package com.geeshow.kakaopay.MoneySpreader.exception;

public class NotEnoughSpreadAmount extends RuntimeException {

    public NotEnoughSpreadAmount(long amount, int ticketCount) {
        super("뿌리기 금액이 충분하지 않습니다. amount:" + amount + " count:" + ticketCount);
    }
}