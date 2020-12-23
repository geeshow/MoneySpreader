package com.geeshow.kakaopay.MoneySpreader.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUser extends BaseEntity {

    // 잔액
    @Column(nullable = false)
    private Long balance;

    public void withdraw(Long amount) {
        this.balance -= amount;
        System.out.println("출금! " + amount);
    }

    public void deposit(Long amount) {
        this.balance += amount;
        System.out.println("입금! " + amount);
    }
}
