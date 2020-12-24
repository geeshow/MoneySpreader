package com.geeshow.kakaopay.MoneySpreader.exception;

public class NotFoundKakaoUserException extends SpreaderException {
    public NotFoundKakaoUserException(long userId) {
        super("존재하지 않는 사용자 ID입니다. userId:" + userId);
    }
}
