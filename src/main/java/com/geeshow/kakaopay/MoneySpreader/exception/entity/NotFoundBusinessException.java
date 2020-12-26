package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;

public class NotFoundBusinessException extends EntityException {
    public NotFoundBusinessException(String token, long userId) {
        super("입력된 조건의 뿌리기가 존재하지 않습니다. 입력값 token:" + token
                + " userId:" + userId);
    }
    public NotFoundBusinessException(String token, String roomId) {
        super("입력된 조건의 뿌리기가 존재하지 않습니다. 입력값 token:" + token
                + " roomId:" + roomId);
    }
}
