package com.geeshow.kakaopay.MoneySpreader.exception.entity;

public class NotFoundKakaoUserNotFoundException extends EntityNotFoundException {
    public NotFoundKakaoUserNotFoundException(long userId) {
        super("존재하지 않는 사용자 ID입니다. userId:" + userId);
    }
}
