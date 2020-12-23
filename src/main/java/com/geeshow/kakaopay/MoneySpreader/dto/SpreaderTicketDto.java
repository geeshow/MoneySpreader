package com.geeshow.kakaopay.MoneySpreader.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

public class SpreaderTicketDto {


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseGet extends RepresentationModel<ResponseGet> {

        private Long userId;
        private Long amount;
    }
}
