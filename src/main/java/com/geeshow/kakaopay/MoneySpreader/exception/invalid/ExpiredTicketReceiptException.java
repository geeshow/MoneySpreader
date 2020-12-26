package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class ExpiredTicketReceiptException extends InvalidException {

    public ExpiredTicketReceiptException(int minutes) {
        super(ErrorCode.ExpiredTicketReceiptException,
                "뿌린 후 "+ minutes + "분"
        );
    }
}
