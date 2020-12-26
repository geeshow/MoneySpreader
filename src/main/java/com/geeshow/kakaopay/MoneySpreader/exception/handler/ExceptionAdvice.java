package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {

        ErrorCode errorCode = ErrorCode.HttpRequestMethodNotSupportedException;
        return new ResponseEntity<>(
                new ErrorResponse(errorCode, e),  errorCode.getStatus());
    }

    /**
     * 필수 헤더값이 누락된 경우 발생
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> handle(MissingRequestHeaderException e) {

        ErrorCode errorCode = ErrorCode.MissingRequestHeaderException;
        return new ResponseEntity<>(
                new ErrorResponse(errorCode, e),  errorCode.getStatus());
    }

    /**
     *  javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     *  HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     *  주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {

        ErrorCode errorCode = ErrorCode.MethodArgumentNotValidException;
        return new ResponseEntity<>(
                    new ErrorResponse(errorCode, e, e.getBindingResult().getFieldErrors()),
                    errorCode.getStatus()
        );
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handle(BusinessException e) {
        return new ResponseEntity<>(new ErrorResponse(e), e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handle(Exception e) {

        ErrorCode errorCode = ErrorCode.UNEXPECTED_EXCEPTION;
        return new ResponseEntity<>(
                new ErrorResponse(errorCode, e),
                errorCode.getStatus()
        );
    }
}
