package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

public class ReceiveOwnTicketException extends InvalidException {

    public ReceiveOwnTicketException(long spreaderUserId) {

        super("본인이 뿌린 돈은 본인이 받을 수 없습니다. 뿌린 사용자 ID:"+ spreaderUserId);
    }
}
