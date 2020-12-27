package com.geeshow.kakaopay.MoneySpreader;

import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class InitWramUp {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbWramUp();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final KakaoUserRepository kakaoUserRepository;
        private final RoomUserRepository roomUserRepository;

        public void dbWramUp() {
            String ROOM_ID1 = "TEST_ROOM_1";
            String ROOM_ID2 = "TEST_ROOM_2";

            // 사용자 등록
            KakaoUser user1 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user2 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user3 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user4 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user5 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user6 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user7 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
            KakaoUser user8 = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());

            // 대화방1 사용자 등록
            roomUserRepository.save(RoomUser.builder().kakaoUser(user1).roomId(ROOM_ID1).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user2).roomId(ROOM_ID1).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user3).roomId(ROOM_ID1).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user4).roomId(ROOM_ID1).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user5).roomId(ROOM_ID1).build());

            // 대화방2 사용자 등록
            roomUserRepository.save(RoomUser.builder().kakaoUser(user1).roomId(ROOM_ID2).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user6).roomId(ROOM_ID2).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user7).roomId(ROOM_ID2).build());
            roomUserRepository.save(RoomUser.builder().kakaoUser(user8).roomId(ROOM_ID2).build());
        }
    }
}

