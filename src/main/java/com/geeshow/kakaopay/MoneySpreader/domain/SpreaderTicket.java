package com.geeshow.kakaopay.MoneySpreader.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpreaderTicket extends BaseEntity {

    // 받은 금액
    @Column(nullable = false)
    private Long amount;

    // 할당 여부
    @Column(nullable = false)
    private Boolean received;

    // Receiver User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private KakaoUser kakaoUser;

    // Spreader User ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreader_id")
    private Spreader spreader;

    public void receiveMoney(KakaoUser receiver) {
        setKakaoUser(receiver);
        setReceived(true);
        kakaoUser.deposit(getAmount());
    }

    public boolean isReceived() {
        return received == true;
    }

}
