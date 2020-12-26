package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

public class NotAllowReadTicketException extends InvalidException {

    public NotAllowReadTicketException(long readUserId) {

        super("뿌린 정보는 본인만 조회할 수 있습니다. 조회 사용자 ID:"+ readUserId);
    }
}
