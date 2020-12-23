package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.exception.ExceedSpreadTicketCount;
import com.geeshow.kakaopay.MoneySpreader.exception.NotFoundKakaoUser;
import com.geeshow.kakaopay.MoneySpreader.exception.NotFoundRoom;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class SpreaderServiceTest {

    @Autowired
    private SpreaderService spreaderService;
    @Autowired
    private SpreaderRepository spreaderRepository;
    @Autowired
    private KakaoUserRepository kakaoUserRepository;
    @Autowired
    private RoomUserRepository roomUserRepository;

    private long _USER_ID;
    private String _ROOM_ID = "X-ROOM-ID-10";

    @BeforeEach
    void setUp() {

        // 뿌리기 사용자 등록
        KakaoUser spreader = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(spreader).roomId(_ROOM_ID).build());

        // 받는 사용자 등록
        IntStream.range(0, 3).forEach(num -> {
            roomUserRepository.save(RoomUser.builder().kakaoUser(
                    kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
            ).roomId(_ROOM_ID).build());
        });

        _USER_ID = spreader.getId();
    }

    @Test
    @DisplayName("뿌리기 테스트 - DB 등록 데이터 검증")
    public void setSpreaderServiceCheckToken() {

        //given
        long amount = 10000;
        int ticketCount = 3;

        //when
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount);
        Spreader spreader = spreaderRepository.findByToken(token)
                .orElseThrow(() -> new AssertionError("뿌리기 조회 오류"));

        //then
        assertThat(token.length()).isEqualTo(3);
        assertThat(spreader.getAmount()).isEqualTo(amount);
        assertThat(spreader.getRoomNumber()).isEqualTo(_ROOM_ID);
        assertThat(spreader.getSpreaderUserId()).isEqualTo(_USER_ID);
        assertThat(spreader.getTicketCount()).isEqualTo(ticketCount);
        assertThat(spreader.getSpreaderTickets().size()).isEqualTo(spreader.getTicketCount());
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(1).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(2).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(0).getReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(1).getReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(2).getReceived()).isFalse();
    }


    @Test
    @DisplayName("뿌리기 테스트 - Ticket DB 데이터 확인")
    public void setSpreaderServiceCheckTickets() {
        //given
        long amount = 123451;
        int ticketCount = 11;

        IntStream.range(0, ticketCount).forEach(num -> {
            roomUserRepository.save(RoomUser.builder().kakaoUser(
                    kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
            ).roomId(_ROOM_ID).build());
        });

        //when
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount);
        Spreader spreader = spreaderRepository.findByToken(token)
                .orElseThrow(() -> new AssertionError("뿌리기 조회 오류"));

        //then
        assertThat(token.length()).isEqualTo(3);
        assertThat(spreader.getAmount()).isEqualTo(amount);
        assertThat(spreader.getRoomNumber()).isEqualTo(_ROOM_ID);
        assertThat(spreader.getSpreaderUserId()).isEqualTo(_USER_ID);
        assertThat(spreader.getTicketCount()).isEqualTo(ticketCount);
        assertThat(spreader.getSpreaderTickets().size()).isEqualTo(spreader.getTicketCount());
        assertThat(spreader.getSpreaderTickets().stream()
                .map(SpreaderTicket::getAmount)
                .reduce(0L, Long::sum))
                .isEqualTo(amount);
    }

    @Test
    @DisplayName("뿌리기 테스트 - 잔액변경 확인")
    public void setSpreaderServiceCheckBalance() {

        //given
        long balance = kakaoUserRepository.findById(this._USER_ID)
                .orElseThrow(() -> new AssertionError("사용자 조회 오류"))
                .getBalance();
        long amount = 10000;
        int ticketCount = 3;

        //when
        spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount);

        //then
        KakaoUser kakaoUser = kakaoUserRepository.findById(this._USER_ID)
                .orElseThrow(() -> new AssertionError("사용자 조회 오류"));

        assertThat(kakaoUser.getBalance()).isEqualTo(balance - amount);
    }

    @Test
    @DisplayName("뿌리기 오류 테스트 - 존재하지 않는 사용자 오류")
    public void setSpreaderNotFoundKakaoUser() {
        //given
        long amount = 123451;
        int ticketCount = 100;
        long notExistUserId = 12130192019L;

        //when
        NotFoundKakaoUser exception = assertThrows(NotFoundKakaoUser.class
                , ()-> spreaderService.spread(_ROOM_ID, notExistUserId, amount, ticketCount));
        String message = exception.getMessage();

        //then
        assertThat(message).contains("존재하지");
   }


    @Test
    @DisplayName("뿌리기 오류 테스트 - 존재하지 않는 룸")
    public void setSpreaderNotFoundRoom() {
        //given
        long amount = 123451;
        int ticketCount = 100;
        String notExistRoomId = "testtesttest111";

        //when
        NotFoundRoom exception = assertThrows(NotFoundRoom.class
                , ()-> spreaderService.spread(notExistRoomId, _USER_ID, amount, ticketCount));
        String message = exception.getMessage();

        //then
        assertThat(message).contains("존재하지");
    }
    @Test
    @DisplayName("뿌리기 오류 테스트 - 뿌리기 건수 초과")
    public void setSpreaderExceedSpreadTicketCount() {
        //given
        long amount = 123451;
        int ticketCount = 1000;

        //when
        ExceedSpreadTicketCount exception = assertThrows(ExceedSpreadTicketCount.class
                , ()-> spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount));
        String message = exception.getMessage();

        //then
        assertThat(message).contains("건수 초과");
    }
}