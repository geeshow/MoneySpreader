package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.geeshow.kakaopay.MoneySpreader.constant.HttpErrorMessages;
import com.geeshow.kakaopay.MoneySpreader.exception.InvalidPathException;
import com.geeshow.kakaopay.MoneySpreader.exception.SpreaderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ApiError> handle(MissingRequestHeaderException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, HttpErrorMessages.MISSING_HEADER_DATA, e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

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
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InvalidPathException.class)
    protected ResponseEntity<ApiError> handle(InvalidPathException e) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getLocalizedMessage(), e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(SpreaderException.class)
    protected ResponseEntity<ApiError> handle(SpreaderException e) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage(), e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handle(Exception e) {
        e.printStackTrace();
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "업무 처리 중 예외적 오류가 발생하였습니다.", e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
