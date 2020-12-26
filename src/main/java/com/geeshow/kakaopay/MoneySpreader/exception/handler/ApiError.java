package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String detailMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Detail> detailErrors;

    public ApiError(HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.message = message;
    }

    public ApiError(HttpStatus status, String message, Throwable e) {
        this(status, message);
        this.detailMessage = e.toString();
    }

    public ApiError(HttpStatus status, String message, Throwable e, List<Detail> detailErrors) {
        this(status, message);
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
