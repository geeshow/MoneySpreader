package com.geeshow.kakaopay.MoneySpreader.repository;

import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {
}
