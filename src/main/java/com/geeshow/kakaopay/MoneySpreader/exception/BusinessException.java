package com.geeshow.kakaopay.MoneySpreader.exception;

import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final String message;
    private final String values;
    private final HttpStatus status;

    public BusinessException(ErrorCode code, String ...values ) {
        super(code.getMessage());
        this.code = code.getCode();
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.values =  Arrays.stream(values).collect(Collectors.joining(", "));
    }
}
