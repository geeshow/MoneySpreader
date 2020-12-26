package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class AlreadyReceivedTicketException extends InvalidException {

    public AlreadyReceivedTicketException(long spreaderUserId, String receiptDate) {
        super(ErrorCode.AlreadyReceivedTicketException,
                "뿌린 사용자 ID:"+ spreaderUserId,
                "받은 날짜:" + receiptDate
        );
    }
}
