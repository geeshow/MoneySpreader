package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class ReceiveOwnTicketException extends InvalidException {

    public ReceiveOwnTicketException(long spreaderUserId) {
        super(ErrorCode.ReceiveOwnTicketException,
                "뿌린 사용자 ID:"+ spreaderUserId
        );
    }
}
