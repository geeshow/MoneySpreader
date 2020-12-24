package com.geeshow.kakaopay.MoneySpreader.exception;

public class NotRemainTicketException extends SpreaderException {
    public NotRemainTicketException(String token) {
        super("뿌려진 모든 금액이 소진되었습니다. token:" + token);
    }
}
