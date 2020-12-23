package com.geeshow.kakaopay.MoneySpreader.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Positive;

public class SpreaderDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {

        @Positive(message = "뿌리기 금액 오류. 양수로 입력해야 합니다.")
        private long amount;

        @Positive(message = "뿌리기 인원 오류. 양수로 입력해야 합니다.")
        private int number;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response extends RepresentationModel<Response> {

        private String token;
    }
}
