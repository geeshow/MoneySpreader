package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotRemainTicketException extends InvalidException {
    public NotRemainTicketException(String roomId) {
        super(ErrorCode.NotRemainTicketException,
                "대화방 ID:" + roomId
        );
    }
}
