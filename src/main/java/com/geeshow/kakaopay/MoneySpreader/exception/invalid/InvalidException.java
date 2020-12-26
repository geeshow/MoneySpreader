package com.geeshow.kakaopay.MoneySpreader.exception.invalid;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;

public class InvalidException extends BusinessException {

    public InvalidException(ErrorCode code, String ...extraErrorInfo ) {
        super(code, extraErrorInfo);
    }
}
