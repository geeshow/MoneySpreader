package com.geeshow.kakaopay.MoneySpreader.exception.entity;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
