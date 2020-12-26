package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class ExpiredReadSpreaderException extends InvalidException {

    public ExpiredReadSpreaderException(String expDate) {
        super(ErrorCode.ExpiredReadSpreaderException,
                "만료일:"+ expDate
        );
    }
}
