package com.geeshow.kakaopay.MoneySpreader.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpreaderDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestSpread {

        @Positive(message = "뿌리기 금액 오류. 양수로 입력해야 합니다.")
        private long amount;

        @Positive(message = "뿌리기 인원 오류. 양수로 입력해야 합니다.")
        private int number;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSpread extends RepresentationModel<ResponseSpread> {

        private String token;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ResponseRead extends RepresentationModel<ResponseRead> {
        private LocalDateTime spreadDatetime;
        private Long spreadAmount;
        private Long receiptAmount;
        private List<TicketDto> receipts;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class TicketDto extends RepresentationModel<TicketDto> {
            private Long userId;
            private Long amount;
        }
    }
}
