package com.geeshow.kakaopay.MoneySpreader.service;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;

public interface SpreaderService {
    /**
     * 대화방에 돈 뿌리기
     *
     * @param roomId 대화방 ID
     * @param userId 사용자 ID
     * @param amount 뿌린 금액
     * @param ticketCount 뿌린 인원
     * @return 뿌리기 token
     */
    Spreader spread(String roomId, long userId, long amount, int ticketCount);

    /**
     * 등록된 뿌리기 조회
     *
     * @param roomId 대화방 ID
     * @param userId 사용자 ID
     * @param token 뿌리기 token
     * @return 뿌리기 현재 상태
     */
    Spreader read(String roomId, long userId, String token);

    /**
     * 뿌린 돈 받기
     *
     * @param roomId 대화방 ID
     * @param userId 사용자 ID
     * @param token 뿌리기 token
     * @return 뿌리기 현재 상태
     */
    SpreaderTicket receive(String roomId, long userId, String token);
}
