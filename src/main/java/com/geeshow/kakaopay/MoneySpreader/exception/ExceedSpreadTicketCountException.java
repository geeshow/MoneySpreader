package com.geeshow.kakaopay.MoneySpreader.exception;

public class ExceedSpreadTicketCountException extends RuntimeException {

    public ExceedSpreadTicketCountException(int possibleCount) {
        super("뿌리기 가능 건수 초과. 가능 건수:" + possibleCount);
    }
}
