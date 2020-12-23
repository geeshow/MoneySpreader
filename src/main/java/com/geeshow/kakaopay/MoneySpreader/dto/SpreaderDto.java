package com.geeshow.kakaopay.MoneySpreader.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SpreaderDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestPost {

        @Positive(message = "뿌리기 금액 오류. 양수로 입력해야 합니다.")
        private long amount;

        @Positive(message = "뿌리기 인원 오류. 양수로 입력해야 합니다.")
        private int number;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponsePost extends RepresentationModel<ResponsePost> {

        private String token;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ResponseGet extends RepresentationModel<ResponseGet> {
        private LocalDateTime spreadDatetime;
        private Long spreadAmount;
        private Long receiptAmount;
        private List<SpreaderTicketDto.ResponseGet> receipts;
    }
}
