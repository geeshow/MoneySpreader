package com.geeshow.kakaopay.MoneySpreader.common;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpreaderUtils {
    public static String generateToken(int size) {
        IntStream numericStream = IntStream.rangeClosed('0','9');
        IntStream lowerStream = IntStream.rangeClosed('a','z');
        IntStream upperStream = IntStream.rangeClosed('A','Z');

        StringBuilder allowedChars = IntStream.concat(
                IntStream.concat(numericStream, lowerStream),
                upperStream
        ).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

        return new SecureRandom()
                .ints(SpreaderConstant.TOKEN_SIZE, 0, allowedChars.length())
                .map(allowedChars::charAt)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }
}
