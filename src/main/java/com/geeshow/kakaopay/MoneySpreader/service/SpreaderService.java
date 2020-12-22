package com.geeshow.kakaopay.MoneySpreader.service;

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
    String spread(String roomId, long userId, long amount, int ticketCount);
}
