package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

public class NotEnoughSpreadAmountException extends InvalidException {

    public NotEnoughSpreadAmountException(long amount, int ticketCount) {
        super("뿌리기 금액이 충분하지 않습니다. amount:" + amount + " count:" + ticketCount);
    }
}
