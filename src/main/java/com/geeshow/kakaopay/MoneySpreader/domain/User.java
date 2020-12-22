package com.geeshow.kakaopay.MoneySpreader.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    // 잔액
    @Column(nullable = false)
    private Long balance;

}
