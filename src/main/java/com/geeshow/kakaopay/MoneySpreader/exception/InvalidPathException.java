package com.geeshow.kakaopay.MoneySpreader.exception;

public class InvalidPathException extends SpreaderException {
    public InvalidPathException(String data) {
        super("PATH Parameter에 필수 값이 누락 되었습니다. 누락 데이터:" + data);
    }
}
