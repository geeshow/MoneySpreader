package com.geeshow.kakaopay.MoneySpreader.exception;

public class NotFoundSpreader extends SpreaderException {
    public NotFoundSpreader(String token, long userId) {
        super("입력된 조건의 뿌리기가 존재하지 않습니다. 입력값 token:" + token
                + " userId:" + userId);
    }
}
