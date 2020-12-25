package com.geeshow.kakaopay.MoneySpreader.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpreaderConstant {
    public static final int TOKEN_SIZE = 3;
    public static final int EXPIRE_DAYS_OF_SPREAD = 7; // 유효 시간: 7일
    public static final int EXPIRE_MINUTES_OF_RECEIPT = 10; // 유효 시간: 7분
    public static final int MINIMUM_SPREAD_AMOUNT = 1; // 최소 뿌리기

    public static final String HTTP_HEADER_USER_ID = "X-USER-ID";
    public static final String HTTP_HEADER_ROOM_ID = "X-ROOM-ID";
}
