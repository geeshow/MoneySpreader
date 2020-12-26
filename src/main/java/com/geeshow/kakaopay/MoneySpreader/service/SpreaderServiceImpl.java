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
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.RandomTicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.utils.token.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SpreaderServiceImpl implements SpreaderService {

    private final KakaoUserRepository kakaoUserRepository;
    private final RoomUserRepository roomUserRepository;
    private final SpreaderRepository spreaderRepository;

    @Override
    @Transactional
    public Spreader spread(String roomId, long userId, long amount, int ticketCount) {

        validateSpread(roomId, userId, amount, ticketCount);

        KakaoUser kakaoUser = kakaoUserRepository.findById(userId).get();

        // TODO KakaoUser Domain 으로 로직 이동
        // 출금 처리
        kakaoUser.withdraw(amount);

        // 뿌리기 티켓 생성
        RandomTicketGenerator ticketGenerator = RandomTicketGenerator.builder()
                .amount(amount)
                .count(ticketCount)
                .minValue(SpreaderConstant.MINIMUM_SPREAD_AMOUNT)
                .build();

        // 뿌리기 생성
        Spreader spreader = Spreader.builder()
                .roomId(roomId)
                .spreaderUserId(kakaoUser.getId())
                .amount(amount)
                .ticketCount(ticketCount)
                .expireReadDate(LocalDateTime.now().plusDays(SpreaderConstant.EXPIRE_DAYS_OF_SPREAD))
                .expireReceiptDate(LocalDateTime.now().plusMinutes(SpreaderConstant.EXPIRE_MINUTES_OF_RECEIPT))
                .token(
                        generateUniqueTokenInRoom(roomId, SpreaderConstant.START_RECURSICE_COUNT)
                )
                .build();

        // 뿌리기 티켓 등록(with 출금 처리)
        spreader.registerTickets(ticketGenerator);

        return spreaderRepository.save(spreader);
    }

    private void validateSpread(String roomId, long userId, long amount, int ticketCount) {

        // 사용자 & 대화방 존재 체크
        checkUserInRoom(roomId, userId);

        // 대화방 사용자 목록
        ArrayList<RoomUser> usersInRoom = roomUserRepository.findByRoomId(roomId).get();

        // 뿌리기 건수 체크
        if ( usersInRoom.size() <= ticketCount )
            throw new ExceedSpreadTicketCountException(usersInRoom.size() - 1);

        if (amount < ticketCount)
            throw new NotEnoughSpreadAmountException(amount, ticketCount);
    }

    private String generateUniqueTokenInRoom(String roomId, int defenseCode) {

        String token = SecureTokenGenerator.generateToken(SpreaderConstant.TOKEN_SIZE);

        if ( defenseCode >= SpreaderConstant.MAXIMUM_RECURSICE_COUNT )
            return token;
        else if ( spreaderRepository.findByRoomIdAndToken(roomId, token).isPresent() )
            return generateUniqueTokenInRoom(roomId, defenseCode++);
        else
            return token;
    }

    @Override
    public Spreader read(String roomId, long userId, String token) {

        Spreader spreader = spreaderRepository.findByRoomIdAndSpreaderUserIdAndToken(roomId, userId, token)
                .orElseThrow(() -> new NotFoundSpreaderException(token, userId));

        if ( spreader.isExpiredRead() ) {
            throw new ExpiredReadSpreaderException(
                    SpreaderDateUtils.parseToDateString(spreader.getExpireReadDate())
            );
        }

        return spreader;
    }

    @Override
    @Transactional
    public SpreaderTicket receive(String roomId, long receiverUserId, String token) {

        validateReceive(roomId, token, receiverUserId);

        // 뿌리기 조회
        Spreader spreader = spreaderRepository.findByRoomIdAndToken(roomId, token).get();

        // 수취인 사용자 조회
        KakaoUser kakaoUser = kakaoUserRepository.findById(receiverUserId).get();

        // 수취 가능한 뿌리기 티켓 획득
        SpreaderTicket receivableTicket = spreader.findReceivableTicket()
                .orElseThrow(() -> new NotRemainTicketException(roomId));

        // 뿌리기 티켓 수취
        receivableTicket.receiveTicket(kakaoUser);

        return receivableTicket;
    }

    private void validateReceive(String roomId, String token, long receiverUserId) {

        // 사용자 & 대화방 존재 체크
        checkUserInRoom(roomId, receiverUserId);

        // 뿌리기 조회
        Spreader spreader = spreaderRepository.findByRoomIdAndToken(roomId, token)
                .orElseThrow(() -> new NotFoundSpreaderException(token, roomId));

        // 수취 만료 시간 확인
        if ( spreader.isExpiredReceive() )
            throw new ExpiredTicketReceiptException(SpreaderConstant.EXPIRE_MINUTES_OF_RECEIPT);

        // 뿌리기 당사자 여부 확인
        if ( spreader.getSpreaderUserId() == receiverUserId )
            throw new ReceiveOwnTicketException(receiverUserId);

        // 중복 수취 여부 확인
        if ( spreader.isReceiverAlready(receiverUserId) ) {
            throw new AlreadyReceivedTicketException(receiverUserId,
                    spreader.findTicketBelongTo(receiverUserId)
                            .map(SpreaderTicket::getReceiptDate)
                            .orElse("")
            );
        }
    }

    private void checkUserInRoom(String roomId, long userId) {
        // 사용자 존재 체크
        KakaoUser kakaoUser = kakaoUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKakaoUserException(userId));

        // 룸 존재 체크
        ArrayList<RoomUser> usersInRoom = roomUserRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundRoomException(roomId));

        // 룸 사용자 체크
        usersInRoom.stream().filter(user -> user.getId() == kakaoUser.getId()).findFirst()
                .orElseThrow(() -> new NotFoundRoomException(roomId, kakaoUser));
    }
}
