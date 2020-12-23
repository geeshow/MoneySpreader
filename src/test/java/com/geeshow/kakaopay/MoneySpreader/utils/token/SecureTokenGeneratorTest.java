package com.geeshow.kakaopay.MoneySpreader.utils.token;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.Collator;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SecureTokenGeneratorTest {

    @Test
    @DisplayName("토큰 생성 테스트 - 길이 일치")
    public void generateTokenForLengthCheck() throws Exception {
        // given
        int len1 = 3;
        int len2 = 5;
        int len3 = 9;

        // when
        String result1 = SecureTokenGenerator.generateToken(len1);
        String result2 = SecureTokenGenerator.generateToken(len2);
        String result3 = SecureTokenGenerator.generateToken(len3);

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);

        // then
        assertTrue(result1.length() == len1);
        assertTrue(result2.length() == len2);
        assertTrue(result3.length() == len3);
    }

    @Test
    @DisplayName("토큰 생성 테스트 - 대문자, 소문자, 숫자 포함")
    public void generateTokenIncludeAllTypeChar() throws Exception {
        // given
        int len1 = 100;

        // when
        String result1 = SecureTokenGenerator.generateToken(len1);
        long count1 = IntStream.rangeClosed('0', '9').filter(num -> result1.indexOf(num) >= 0).count();
        long count2 = IntStream.rangeClosed('a', 'z').filter(num -> result1.indexOf(num) >= 0).count();
        long count3 = IntStream.rangeClosed('A', 'Z').filter(num -> result1.indexOf(num) >= 0).count();

        System.out.println(result1);

        // then
        assertTrue(count1 > 0);
        assertTrue(count2 > 0);
        assertTrue(count3 > 0);
    }

    @Test
    @DisplayName("토큰 생성 테스트 - 두 생성 값의 불일치")
    public void generateTokenNotSame() throws Exception {
        // given
        int len1 = 3;
        int len2 = 3;

        // when
        String result1 = SecureTokenGenerator.generateToken(len1);
        String result2 = SecureTokenGenerator.generateToken(len2);

        System.out.println(result1);
        System.out.println(result2);

        // then
        assertTrue(!result1.equals(result2));
    }


    @Test
    @DisplayName("토큰 생성 테스트 - 모든 문자 발생 여부 테스트")
    public void generateTokenUseAllChar() throws Exception {
        // given
        int len1 = 10000;

        // when
        String result1 = SecureTokenGenerator.generateToken(len1);
        long count1 = IntStream.rangeClosed('0', '9').filter(num -> result1.indexOf(num) >= 0).count();
        long count2 = IntStream.rangeClosed('a', 'z').filter(num -> result1.indexOf(num) >= 0).count();
        long count3 = IntStream.rangeClosed('A', 'Z').filter(num -> result1.indexOf(num) >= 0).count();

        System.out.println(count1);
        System.out.println(count2);
        System.out.println(count3);

        // then
        assertTrue(count1 == IntStream.rangeClosed('0', '9').count());
        assertTrue(count2 == IntStream.rangeClosed('a', 'z').count());
        assertTrue(count3 == IntStream.rangeClosed('A', 'Z').count());
    }

    @Test
    @DisplayName("토큰 생성 테스트 - 모든 문자 발생 여부 테스트")
    public void generateTokenCheckUnexpectedChar() throws Exception {
        // given
        int len1 = 10000;

        // when
        String result1 = SecureTokenGenerator.generateToken(len1);
        long count1 = IntStream.rangeClosed('0', '9').filter(num -> result1.indexOf(num) == -1).count();
        long count2 = IntStream.rangeClosed('a', 'z').filter(num -> result1.indexOf(num) == -1).count();
        long count3 = IntStream.rangeClosed('A', 'Z').filter(num -> result1.indexOf(num) == -1).count();

        System.out.println(count1);
        System.out.println(count2);
        System.out.println(count3);

        // then
        assertTrue(count1 == 0);
        assertTrue(count2 == 0);
        assertTrue(count3 == 0);
    }
}