package com.geeshow.kakaopay.MoneySpreader.utils.token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecureTokenGenerator {
    private static final String allowedStringForToken = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateToken(int length) {
        return new SecureRandom()
                .ints(length, 0, allowedStringForToken.length())
                .map(allowedStringForToken::charAt)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }
}
