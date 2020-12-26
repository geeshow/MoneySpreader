package com.geeshow.kakaopay.MoneySpreader.exception.entity;

public class NotFoundSpreaderException extends EntityNotFoundException {
    public NotFoundSpreaderException(String token, String roomId) {
        super("입력된 조건의 뿌리기가 존재하지 않습니다. 입력값 token:" + token
                + " roomId:" + roomId);
    }
}
