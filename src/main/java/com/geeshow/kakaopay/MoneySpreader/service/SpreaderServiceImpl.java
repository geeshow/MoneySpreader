package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.exception.*;
import com.geeshow.kakaopay.MoneySpreader.utils.date.SpreaderDateUtils;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.RandomTicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
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
    public String spread(String roomId, long userId, long amount, int ticketCount) {

        validateSpread(roomId, userId, amount, ticketCount);

        KakaoUser kakaoUser = kakaoUserRepository.findById(userId).get();

        // 출금 처리
        kakaoUser.withdraw(amount);

        // 뿌리기 생성
        Spreader spreader = Spreader.builder()
                .roomNumber(roomId)
                .spreaderUserId(kakaoUser.getId())
                .amount(amount)
                .ticketCount(ticketCount)
                .expiredDate(LocalDateTime.now().plusDays(SpreaderConstant.PERIOD_OF_EXPIRE_SPREAD))
                .token(
                        SecureTokenGenerator.generateToken(SpreaderConstant.TOKEN_SIZE)
                )
                .build();

        // 뿌리기 티켓 생성
        spreader.registeTickets(new RandomTicketGenerator());

        return spreaderRepository.save(spreader).getToken();
    }

    private void validateSpread(String roomId, long userId, long amount, int ticketCount) {

        // 사용자 존재 체크
        KakaoUser kakaoUser = kakaoUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKakaoUser(userId));

        // 룸 존재 체크
        ArrayList<RoomUser> usersInRoom = roomUserRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundRoom(roomId));

        // 룸 사용자 체크
        usersInRoom.stream().filter(user -> user.getId() == kakaoUser.getId()).findFirst()
                .orElseThrow(() -> new NotFoundRoom(roomId, kakaoUser));

        // 뿌리기 건수 체크
        if ( usersInRoom.size() <= ticketCount )
            throw new ExceedSpreadTicketCount(usersInRoom.size() - 1);

        if (amount < ticketCount)
            throw new NotEnoughSpreadAmount(amount, ticketCount);
    }

    @Override
    public Spreader read(String token, long userId) {

        Spreader spreader = spreaderRepository.findByTokenAndSpreaderUserId(token, userId)
                .orElseThrow(() -> new NotFoundSpreader(token, userId));

        if ( spreader.isExpired() ) {
            throw new ExpiredReadSpreader(
                    SpreaderDateUtils.parseToDateString(spreader.getExpiredDate())
            );
        }

        return spreader;
    }
}
