package com.geeshow.kakaopay.MoneySpreader.repository;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpreaderRepository extends JpaRepository<Spreader, Long> {
    @EntityGraph(attributePaths = "spreaderTickets")
    Optional<Spreader> findByToken(String token);

    @EntityGraph(attributePaths = "spreaderTickets")
    Optional<Spreader> findByRoomIdAndToken(String roomId, String token);

}
