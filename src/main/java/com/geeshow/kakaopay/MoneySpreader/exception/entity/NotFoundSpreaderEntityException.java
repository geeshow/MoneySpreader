package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotFoundSpreaderEntityException extends NotFoundEntityException {
    public NotFoundSpreaderEntityException(String token, String roomId) {
        super(ErrorCode.NotFoundSpreaderEntityException,
                "token:" + token,
                "대화방 ID:" + roomId);
    }
}
