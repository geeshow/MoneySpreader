package com.geeshow.kakaopay.MoneySpreader.exception;

public class ExpiredReadSpreader extends RuntimeException {

    public ExpiredReadSpreader(String expDate) {

        super("뿌리기 조회 가능일이 만료되었습니다. 만료일:"+ expDate);
    }
}
