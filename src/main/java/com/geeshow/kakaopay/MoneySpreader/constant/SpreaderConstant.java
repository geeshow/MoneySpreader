package com.geeshow.kakaopay.MoneySpreader.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpreaderConstant {
    public static final int TOKEN_SIZE = 3;
    public static final int PERIOD_OF_EXPIRE_SPREAD = 7; // 유효 시간: 7일
    public static final int MINIMUM_SPREAD_AMOUNT = 1; // 최소 뿌리기
}
