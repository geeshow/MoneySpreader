package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class ExceedSpreadTicketCountException extends InvalidException {

    public ExceedSpreadTicketCountException(int possibleCount) {
        super(ErrorCode.ExceedSpreadTicketCountException,
                "가능 건수:" + possibleCount
        );
    }
}
