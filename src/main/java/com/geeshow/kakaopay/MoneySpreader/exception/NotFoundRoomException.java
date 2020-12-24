package com.geeshow.kakaopay.MoneySpreader.exception;

import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;

public class NotFoundRoomException extends SpreaderException {
    public NotFoundRoomException(String roomId) {
        super("존재하지 않는 룸 입니다. roomId:" + roomId);
    }

    public NotFoundRoomException(String roomId, KakaoUser kakaoUser) {
        super("해당 룸에 존재하지 않는 사용자 ID입니다. roomId:" + roomId
                + " userId:" + kakaoUser.getId());
    }
}