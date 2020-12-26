package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotFoundUserInRoomEntityException extends NotFoundEntityException {
    public NotFoundUserInRoomEntityException(String roomId, long userId) {
        super(ErrorCode.NotFoundUserInRoomEntityException,
                "대화방 ID:" + roomId,
                "사용자 ID:" + userId
        );
    }
}
