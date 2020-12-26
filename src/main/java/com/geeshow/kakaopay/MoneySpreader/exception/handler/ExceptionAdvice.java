package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.geeshow.kakaopay.MoneySpreader.constant.HttpErrorMessages;
import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import com.geeshow.kakaopay.MoneySpreader.exception.entity.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiError> handle(HttpRequestMethodNotSupportedException e) {

        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, HttpErrorMessages.MISSING_HEADER_DATA, e);
        return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 필수 헤더값이 누락된 경우 발생
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ApiError> handle(MissingRequestHeaderException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, HttpErrorMessages.MISSING_HEADER_DATA, e);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     *  javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     *  HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     *  주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiError> handle(MethodArgumentNotValidException e) {
        List<ApiError.Detail> subErrors =
                e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(fieldError
                                -> ApiError.Detail.builder()
                                    .object(fieldError.getObjectName())
                                    .field(fieldError.getField())
                                    .rejectedValue(fieldError.getRejectedValue())
                                    .message(fieldError.getDefaultMessage())
                                    .build())
                        .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST
                , HttpErrorMessages.INVALID_BODY_DATA
                , e
                , subErrors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiError> handle(EntityNotFoundException e) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getLocalizedMessage(), e);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiError> handle(BusinessException e) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage(), e);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handle(Exception e) {
        e.printStackTrace();
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "업무 처리 중 예외적 오류가 발생하였습니다.", e);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
