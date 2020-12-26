package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotFoundKakaoUserEntityException extends NotFoundEntityException {
    public NotFoundKakaoUserEntityException(long userId) {
        super(ErrorCode.NotFoundKakaoUserEntityException, "사용자 ID:" + userId);
    }
}
