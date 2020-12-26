package com.geeshow.kakaopay.MoneySpreader.exception.handler;

import com.geeshow.kakaopay.MoneySpreader.exception.BusinessException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Entity 오류
     NotFoundKakaoUserEntityException("E001", "존재하지 않는 사용자 ID입니다.", HttpStatus.NOT_FOUND)
    ,NotFoundUserInRoomEntityException("E002", "해당 룸에 존재하지 않는 사용자 ID입니다.", HttpStatus.NOT_FOUND)
    ,NotFoundRoomEntityException("E003", "존재하지 않는 룸 입니다.", HttpStatus.NOT_FOUND)
    ,NotFoundSpreaderEntityException("E004", "입력된 조건의 뿌리기가 존재하지 않습니다.", HttpStatus.NOT_FOUND)

    // 업무 오류
    ,AlreadyReceivedTicketException("B001", "뿌린 돈은 한번만 수령 가능 합니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,ExceedSpreadTicketCountException("B002", "뿌리기 가능 건수 초과.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,ExpiredReadSpreaderException("B003", "뿌리기 조회 가능일이 만료되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,ExpiredTicketReceiptException("B004", "뿌리기 수령 가능 시간이 초과했습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,NotAllowReadTicketException("B005", "뿌린 정보는 본인만 조회할 수 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,NotEnoughSpreadAmountException("B006", "뿌리기 금액이 충분하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,NotRemainTicketException("B007", "뿌려진 모든 금액이 소진되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ,ReceiveOwnTicketException("B008", "본인이 뿌린 돈은 본인이 받을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR)

    // HTTP 오류
    ,HttpRequestMethodNotSupportedException("H001", "지원하지 않은 HTTP method 호출입니다.", HttpStatus.METHOD_NOT_ALLOWED)
    ,MissingRequestHeaderException("H002", "필수 헤더값이 누락되었습니다.", HttpStatus.BAD_REQUEST)
    ,MethodArgumentNotValidException("H003", "필수 입력값이 누락되었습니다.", HttpStatus.BAD_REQUEST)

    // 예외 오류 발생
    ,UNEXPECTED_EXCEPTION("Z999", "업무 처리 중 예외적 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
