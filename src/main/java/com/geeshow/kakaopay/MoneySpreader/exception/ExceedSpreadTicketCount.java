package com.geeshow.kakaopay.MoneySpreader.exception;

public class ExceedSpreadTicketCount extends RuntimeException {

    public ExceedSpreadTicketCount(int possibleCount) {
        super("뿌리가 가능 건수 초과. 가능 건수:" + possibleCount);
    }
}
