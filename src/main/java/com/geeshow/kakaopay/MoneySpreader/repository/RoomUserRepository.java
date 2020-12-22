package com.geeshow.kakaopay.MoneySpreader.repository;

import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {
    Optional<RoomUser> findByRoomIdAndUser(String roomId, KakaoUser kakaoUser);
}
