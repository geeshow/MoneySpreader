package com.geeshow.kakaopay.MoneySpreader.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiver extends BaseEntity {

    // 받은 금액
    @Column(nullable = false)
    private Long amount;

    // Receiver User
    @Column
    private Integer userId;

    // Spreader User ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreader_id")
    private Spreader spreader;

}
