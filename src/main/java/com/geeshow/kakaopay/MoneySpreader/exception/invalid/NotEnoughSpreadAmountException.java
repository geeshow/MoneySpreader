package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotEnoughSpreadAmountException extends InvalidException {

    public NotEnoughSpreadAmountException(long amount, int ticketCount) {
        super(ErrorCode.NotEnoughSpreadAmountException,
                "뿌린 금액:" + amount,
                "뿌리기 건수:" + ticketCount
        );
    }
}
