package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.exception.entity.NotFoundKakaoUserEntityException;
import com.geeshow.kakaopay.MoneySpreader.exception.entity.NotFoundRoomEntityException;
import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.ExceedSpreadTicketCountException;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.ExpiredReadSpreaderException;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.NotRemainTicketException;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
import com.geeshow.kakaopay.MoneySpreader.utils.date.SpreaderDateUtils;
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
    private String _ROOM_ID = "X-ROOM-ID-91";

    @BeforeEach
    void setUp() {

        // 사용자 등록
        KakaoUser spreader = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
        KakaoUser receiver1 = kakaoUserRepository.save(KakaoUser.builder().balance(0L).build());
        KakaoUser receiver2 = kakaoUserRepository.save(KakaoUser.builder().balance(0L).build());
        KakaoUser receiver3 = kakaoUserRepository.save(KakaoUser.builder().balance(0L).build());

        // 대화방 등록
        roomUserRepository.save(RoomUser.builder().kakaoUser(spreader).roomId(_ROOM_ID).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(receiver1).roomId(_ROOM_ID).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(receiver2).roomId(_ROOM_ID).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(receiver3).roomId(_ROOM_ID).build());

        // 테스트 변수 설정
        _USER_ID = spreader.getId();
        _RECEIVER_USER_ID1 = receiver1.getId();
        _RECEIVER_USER_ID2 = receiver2.getId();
        _RECEIVER_USER_ID3 = receiver3.getId();
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
        NotFoundKakaoUserEntityException exception = assertThrows(NotFoundKakaoUserEntityException.class
                , ()-> spreaderService.spread(_ROOM_ID, notExistUserId, amount, ticketCount));

        //then
        assertThat(exception.getMessage()).contains(ErrorCode.NotFoundKakaoUserEntityException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.NotFoundKakaoUserEntityException.getCode());

    }


    @Test
    @DisplayName("뿌리기 오류 테스트 - 존재하지 않는 룸")
    public void setSpreaderNotFoundRoom() {
        //given
        long amount = 123451;
        int ticketCount = 100;
        String notExistRoomId = "testtesttest111";

        //when
        NotFoundRoomEntityException exception = assertThrows(NotFoundRoomEntityException.class
                , ()-> spreaderService.spread(notExistRoomId, _USER_ID, amount, ticketCount));

        //then
        assertThat(exception.getMessage()).contains(ErrorCode.NotFoundRoomEntityException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.NotFoundRoomEntityException.getCode());
    }

    @Test
    @DisplayName("뿌리기 오류 테스트 - 뿌리기 건수 초과")
    public void setSpreaderExceedSpreadTicketCount() {
        //given
        long amount = 123451;
        int ticketCount = 1000;

        //when & then
        ExceedSpreadTicketCountException exception = assertThrows(ExceedSpreadTicketCountException.class
                , ()-> spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount));

        assertThat(exception.getMessage()).contains(ErrorCode.ExceedSpreadTicketCountException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.ExceedSpreadTicketCountException.getCode());
    }

    @Test
    @DisplayName("뿌리기 조회 테스트")
    public void readSpreaderTest() {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        Spreader spreader = spreaderService.read(_ROOM_ID, _USER_ID, token);

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
        LocalDateTime pastExpireReadDate = LocalDateTime.now().minusDays(SpreaderConstant.EXPIRE_DAYS_OF_SPREAD);
        Spreader spreader = spreaderService.spread(_ROOM_ID, _USER_ID, 10000L, 3);
        spreader.setExpireReadDate(pastExpireReadDate);
        spreaderRepository.save(spreader);

        //when & then
        ExpiredReadSpreaderException exception = assertThrows(ExpiredReadSpreaderException.class
                , ()-> spreaderService.read(_ROOM_ID, _USER_ID, spreader.getToken()));

        assertThat(exception.getMessage()).contains(ErrorCode.ExpiredReadSpreaderException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.ExpiredReadSpreaderException.getCode());
    }


    @Test
    @DisplayName("받기 테스트 - 정상받기")
    public void reciptTestReceivableTicket() {
        //given
        long amount = 10000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        long receive1 = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, token).getAmount();
        long receive2 = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID2, token).getAmount();
        long receive3 = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID3, token).getAmount();

        //then
        Spreader spreader = spreaderRepository.findByRoomIdAndToken(_ROOM_ID, token).get();
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