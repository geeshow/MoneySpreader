package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.exception.*;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
import com.geeshow.kakaopay.MoneySpreader.utils.date.SpreaderDateUtils;
import com.geeshow.kakaopay.MoneySpreader.utils.token.SecureTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
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
    private long _RECEIVER_USER_ID1;
    private long _RECEIVER_USER_ID2;
    private long _RECEIVER_USER_ID3;
    private String _ROOM_ID = "X-ROOM-ID-10";

    @BeforeEach
    void setUp() {

        // 뿌리기 사용자 등록
        KakaoUser spreader = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(spreader).roomId(_ROOM_ID).build());

        // 받는 사용자 등록
        _RECEIVER_USER_ID1 = roomUserRepository.save(
                RoomUser.builder().kakaoUser(
                    kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
                ).roomId(_ROOM_ID).build()
        ).getId();

        _RECEIVER_USER_ID2 = roomUserRepository.save(
                RoomUser.builder().kakaoUser(
                        kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
                ).roomId(_ROOM_ID).build()
        ).getId();

        _RECEIVER_USER_ID3 = roomUserRepository.save(
                RoomUser.builder().kakaoUser(
                        kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
                ).roomId(_ROOM_ID).build()
        ).getId();

        _USER_ID = spreader.getId();
    }

    @Test
    @DisplayName("뿌리기 테스트 - DB 등록 데이터 검증")
    public void setSpreaderServiceCheckToken() {

        //given
        long amount = 10000;
        int ticketCount = 3;

        //when
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();
        Spreader spreader = spreaderRepository.findByToken(token)
                .orElseThrow(() -> new AssertionError("뿌리기 조회 오류"));

        //then
        assertThat(token.length()).isEqualTo(SpreaderConstant.TOKEN_SIZE);
        assertThat(spreader.getAmount()).isEqualTo(amount);
        assertThat(spreader.getRoomId()).isEqualTo(_ROOM_ID);
        assertThat(spreader.getSpreaderUserId()).isEqualTo(_USER_ID);
        assertThat(spreader.getTicketCount()).isEqualTo(ticketCount);
        assertThat(spreader.getExpireReadDate()).isAfter(LocalDateTime.now());
        assertThat(spreader.getSpreaderTickets().size()).isEqualTo(spreader.getTicketCount());
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(1).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(2).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(0).isReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(1).isReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(2).isReceived()).isFalse();
        assertThat(spreader.getReceivedTickets().size()).isEqualTo(0);
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
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();
        Spreader spreader = spreaderRepository.findByToken(token)
                .orElseThrow(() -> new AssertionError("뿌리기 조회 오류"));

        //then
        assertThat(token.length()).isEqualTo(SpreaderConstant.TOKEN_SIZE);
        assertThat(spreader.getAmount()).isEqualTo(amount);
        assertThat(spreader.getRoomId()).isEqualTo(_ROOM_ID);
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
        NotFoundKakaoUserException exception = assertThrows(NotFoundKakaoUserException.class
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
        NotFoundRoomException exception = assertThrows(NotFoundRoomException.class
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
        ExceedSpreadTicketCountException exception = assertThrows(ExceedSpreadTicketCountException.class
                , ()-> spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount));
        String message = exception.getMessage();

        //then
        assertThat(message).contains("건수 초과");
    }

    @Test
    @DisplayName("뿌리기 조회 테스트")
    public void readSpreaderTest() {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        Spreader spreader = spreaderService.read(token, _USER_ID);

        //then
        assertThat(spreader.getToken().length()).isEqualTo(SpreaderConstant.TOKEN_SIZE);
        assertThat(spreader.getAmount()).isGreaterThan(0L);
        assertThat(spreader.getRoomId()).isEqualTo(_ROOM_ID);
        assertThat(spreader.getSpreaderUserId()).isEqualTo(_USER_ID);
        assertThat(spreader.getTicketCount()).isGreaterThan(0);
        assertThat(spreader.getExpireReadDate()).isAfter(LocalDateTime.now());
        assertThat(spreader.getSpreaderTickets().size()).isEqualTo(spreader.getTicketCount());
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(1).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(2).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().stream()
                .map(SpreaderTicket::getAmount)
                .reduce(0L, Long::sum))
                .isEqualTo(spreader.getAmount());
    }

    @Test
    @DisplayName("뿌리기 조회 오류 테스트 - 뿌리기 조회 기간 초과")
    public void readExpiredSpreader() {
        //given

        //when
        Spreader spreader = Spreader.builder()
                .roomId("TEST-SPREADER-READ")
                .spreaderUserId(_USER_ID)
                .amount(10000L)
                .ticketCount(4)
                .expireReadDate(LocalDateTime.now().minusDays(SpreaderConstant.EXPIRE_DAYS_OF_SPREAD))
                .expireReceiptDate(LocalDateTime.now().minusDays(SpreaderConstant.EXPIRE_MINUTES_OF_RECEIPT))
                .token(
                        SecureTokenGenerator.generateToken(SpreaderConstant.TOKEN_SIZE)
                )
                .build();
        spreaderRepository.save(spreader);

        //then
        ExpiredReadSpreaderException exception = assertThrows(ExpiredReadSpreaderException.class
                , ()-> spreaderService.read(spreader.getToken(), _USER_ID));
        String message = exception.getMessage();
        assertThat(message).contains("만료");
    }


    @Test
    @DisplayName("받기 테스트 - 정상받기")
    public void reciptTestReceivableTicket() {
        //given
        long amount = 10000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        long receive1 = spreaderService.receive(_ROOM_ID, token, _RECEIVER_USER_ID1);
        long receive2 = spreaderService.receive(_ROOM_ID, token, _RECEIVER_USER_ID2);
        long receive3 = spreaderService.receive(_ROOM_ID, token, _RECEIVER_USER_ID3);

        //then
        Spreader spreader = spreaderRepository.findByTokenAndRoomId(token, _ROOM_ID).get();
        assertThat(receive1+receive2+receive3).isEqualTo(amount);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID1).get().getReceiverUserId()).isEqualTo(_RECEIVER_USER_ID1);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID2).get().getReceiverUserId()).isEqualTo(_RECEIVER_USER_ID2);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID3).get().getReceiverUserId()).isEqualTo(_RECEIVER_USER_ID3);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID1).get().getAmount()).isEqualTo(receive1);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID2).get().getAmount()).isEqualTo(receive2);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID3).get().getAmount()).isEqualTo(receive3);
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID1).get().getReceiptDate()).isEqualTo(SpreaderDateUtils.getToday());
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID2).get().getReceiptDate()).isEqualTo(SpreaderDateUtils.getToday());
        assertThat(spreader.findTicketBelongTo(_RECEIVER_USER_ID3).get().getReceiptDate()).isEqualTo(SpreaderDateUtils.getToday());
        assertThrows(NotRemainTicketException.class, () -> spreader.findReceivableTicket().orElseThrow(()->new NotRemainTicketException(_ROOM_ID)));
    }

}