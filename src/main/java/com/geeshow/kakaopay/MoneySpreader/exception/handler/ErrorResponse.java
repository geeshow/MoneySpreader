package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import lombok.*;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detailMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Detail> detailErrors;

    public ErrorResponse(BusinessException e) {
        this.timestamp = LocalDateTime.now();
        this.status = e.getStatus().value();
        this.message = e.getMessage();
        this.code = e.getCode();
        this.detailMessage = e.toString();
    }

    public ErrorResponse(ErrorCode errorCode, Throwable e) {
        this.timestamp = LocalDateTime.now();
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.detailMessage = e.toString();
    }

    public ErrorResponse(ErrorCode errorCode, Throwable e, List<FieldError> fieldErrors) {
        this(errorCode, e);
        this.detailErrors = fieldErrors
                            .stream()
                            .map(fieldError
                                    -> ErrorResponse.Detail.builder()
                                        .object(fieldError.getObjectName())
                                        .field(fieldError.getField())
                                        .rejectedValue(fieldError.getRejectedValue())
                                        .message(fieldError.getDefaultMessage())
                                        .build())
                            .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private String object;
        private String field;
        private Object rejectedValue;
        private String message;
    }
}
