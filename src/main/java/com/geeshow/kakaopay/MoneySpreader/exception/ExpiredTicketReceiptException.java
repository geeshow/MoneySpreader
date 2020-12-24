package com.geeshow.kakaopay.MoneySpreader.exception;

public class ExpiredTicketReceiptException extends RuntimeException {

    public ExpiredTicketReceiptException(int minutes) {

        super("뿌리기 수령 가능 시간이 초과했습니다. 뿌린 후 :"+ minutes + "분");
    }
}
