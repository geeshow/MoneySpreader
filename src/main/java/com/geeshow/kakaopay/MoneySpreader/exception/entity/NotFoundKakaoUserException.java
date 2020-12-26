package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;

public class NotFoundKakaoUserException extends EntityException {
    public NotFoundKakaoUserException(long userId) {
        super("존재하지 않는 사용자 ID입니다. userId:" + userId);
    }
}
