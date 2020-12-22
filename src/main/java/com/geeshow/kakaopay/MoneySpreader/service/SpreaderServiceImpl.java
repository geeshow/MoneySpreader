package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.utils.ticket.RandomTicketGenerator;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.exception.NotFoundKakaoUser;
import com.geeshow.kakaopay.MoneySpreader.exception.NotFoundRoomUser;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
import com.geeshow.kakaopay.MoneySpreader.utils.token.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpreaderServiceImpl implements SpreaderService {

    private final KakaoUserRepository kakaoUserRepository;
    private final RoomUserRepository roomUserRepository;
    private final SpreaderRepository spreaderRepository;

    @Override
    @Transactional
    public String spread(String roomId, long userId, long amount, int ticketCount) {

        KakaoUser kakaoUser = kakaoUserRepository.findById(userId)
                .orElseThrow(() -> new NotFoundKakaoUser(userId));

        RoomUser roomUser = roomUserRepository.findByRoomIdAndUser(roomId, kakaoUser)
                .orElseThrow(() -> new NotFoundRoomUser(roomId, kakaoUser));

        Spreader spreader = Spreader.builder()
                .roomUser(roomUser)
                .amount(amount)
                .ticketCount(ticketCount)
                .token(
                        SecureTokenGenerator.generateToken(SpreaderConstant.TOKEN_SIZE)
                )
                .build();

        spreader.registeTickets(new RandomTicketGenerator());

        return "token";
    }
}
