package com.geeshow.kakaopay.MoneySpreader.exception;

public class AlreadyReceivedTicketException extends RuntimeException {

    public AlreadyReceivedTicketException(long spreaderUserId, String receiptDate) {

        super("뿌린 돈은 한번만 수령 가능 합니다. 뿌린 사용자 ID:"+ spreaderUserId +
                "받은 날짜:" + receiptDate
        );
    }
}
