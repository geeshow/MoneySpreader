package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

public class NotRemainTicketException extends InvalidException {
    public NotRemainTicketException(String roomId) {
        super("뿌려진 모든 금액이 소진되었습니다. roomId:" + roomId);
    }
}
