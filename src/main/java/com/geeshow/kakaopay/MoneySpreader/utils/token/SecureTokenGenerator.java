package com.geeshow.kakaopay.MoneySpreader.utils.token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecureTokenGenerator {
    public static String generateToken(int length) {
        IntStream numericStream = IntStream.rangeClosed('0','9');
        IntStream lowerStream = IntStream.rangeClosed('a','z');
        IntStream upperStream = IntStream.rangeClosed('A','Z');

        StringBuilder allowedChars = IntStream.concat(
                IntStream.concat(numericStream, lowerStream),
                upperStream
        ).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

        return new SecureRandom()
                .ints(length, 0, allowedChars.length())
                .map(allowedChars::charAt)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }
}
