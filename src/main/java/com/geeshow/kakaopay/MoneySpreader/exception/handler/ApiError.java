package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detailMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Detail> detailErrors;

    public ApiError(HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, Throwable e) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.detailMessage = e.toString();
    }

    public ApiError(HttpStatus status, String message, Throwable e, List<Detail> detailErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.detailMessage = e.toString();
        this.detailErrors = detailErrors;
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
