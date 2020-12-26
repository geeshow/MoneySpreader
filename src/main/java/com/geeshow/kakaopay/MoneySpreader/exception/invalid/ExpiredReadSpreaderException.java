package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

public class ExpiredReadSpreaderException extends InvalidException {

    public ExpiredReadSpreaderException(String expDate) {

        super("뿌리기 조회 가능일이 만료되었습니다. 만료일:"+ expDate);
    }
}
