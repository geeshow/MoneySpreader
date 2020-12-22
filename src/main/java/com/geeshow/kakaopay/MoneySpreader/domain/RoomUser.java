package com.geeshow.kakaopay.MoneySpreader.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomUser extends BaseEntity {

    // Room ID
    @Column(nullable = false)
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private KakaoUser kakaoUser;
    
}
