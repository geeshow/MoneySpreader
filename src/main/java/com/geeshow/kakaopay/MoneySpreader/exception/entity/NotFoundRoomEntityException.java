package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotFoundRoomEntityException extends NotFoundEntityException {
    public NotFoundRoomEntityException(String roomId) {
        super(ErrorCode.NotFoundRoomEntityException,
                "대화방 ID:" + roomId);
    }
}
