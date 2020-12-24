package com.geeshow.kakaopay.MoneySpreader.repository;

import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface RoomUserRepository extends JpaRepository<RoomUser, Long> {
    Optional<ArrayList<RoomUser>> findByRoomId(String roomId);
}
