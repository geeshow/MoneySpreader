package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class NotFoundEntityException extends BusinessException {

    public NotFoundEntityException(ErrorCode code, String ...values ) {
        super(code, values);
    }
}
