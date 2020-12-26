package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotAllowReadTicketException extends InvalidException {

    public NotAllowReadTicketException(long readUserId) {
        super(ErrorCode.NotAllowReadTicketException,
                "조회 사용자 ID:"+ readUserId
        );
    }
}
