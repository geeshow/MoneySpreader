package com.geeshow.kakaopay.MoneySpreader.requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.SpreaderRepository;
import com.geeshow.kakaopay.MoneySpreader.service.SpreaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.config.docs-host}")
@ActiveProfiles("test")
public class ReadApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired private KakaoUserRepository kakaoUserRepository;
    @Autowired private RoomUserRepository roomUserRepository;
    @Autowired private SpreaderRepository spreaderRepository;
    @Autowired
    private SpreaderService spreaderService;

    private long _USER_ID;
    private long _RECEIVER_USER_ID1;
    private long _RECEIVER_USER_ID2;
    private long _RECEIVER_USER_ID3;
    private String _ROOM_ID = "X-ROOM-ID-50";

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
    @DisplayName("조회 API 요건1 - 뿌리기 시 발급된 token을 요청값으로 받습니다.")
    void checkReadRequirement1() throws Exception {
        //given
        long amount = 40000000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when : 토큰을 넘기지 않고 받기 API를 호출
        final ResultActions actions =
                mockMvc.perform(
                        get("/v1/spreader/")
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _RECEIVER_USER_ID1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));
        //then : 오류 발생
        actions
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(HttpStatus.METHOD_NOT_ALLOWED.value()))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("detailMessage").exists())
        ;
    }


    @Test
    @DisplayName("조회 API 요건2&3 - token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다.")
    void checkReadRequirement2() throws Exception {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();
        SpreaderTicket receive1 = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, token);
        SpreaderTicket receive2 = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID2, token);

        //when
        final ResultActions actions =
                mockMvc.perform(
                        get("/v1/spreader/{token}", token)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("spreadDatetime").isNotEmpty())
                .andExpect(jsonPath("spreadAmount").value(amount))
                .andExpect(jsonPath("receiptAmount").value(receive1.getAmount() + receive2.getAmount()))
                .andExpect(jsonPath("receipts[0].userId").value(receive1.getReceiverUserId()))
                .andExpect(jsonPath("receipts[0].amount").value(receive1.getAmount()))
                .andExpect(jsonPath("receipts[1].userId").value(receive2.getReceiverUserId()))
                .andExpect(jsonPath("receipts[1].amount").value(receive2.getAmount()))
                .andExpect(jsonPath("receipts[2]").doesNotExist())
        ;
    }


    @Test
    @DisplayName("조회 API 요건4-1 - 뿌린 사람 자신만 조회를 할 수 있습니다.")
    void checkReadRequirement4() throws Exception {
        //given
        long amount = 40000000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        get("/v1/spreader/" + token)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _RECEIVER_USER_ID1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));
        //then : 오류 발생
        actions
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(ErrorCode.NotAllowReadTicketException.getStatus().value()))
                .andExpect(jsonPath("code").value(ErrorCode.NotAllowReadTicketException.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NotAllowReadTicketException.getMessage()))
                .andExpect(jsonPath("detailMessage").exists())
        ;
    }

    @Test
    @DisplayName("조회 API 요건4-2 - 뿌린 사람 자신만 조회를 할 수 있습니다.")
    void checkReadRequirement4_2() throws Exception {
        //given
        long amount = 40000000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        get("/v1/spreader/NOT")
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));
        //then : 오류 발생
        actions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(ErrorCode.NotFoundSpreaderEntityException.getStatus().value()))
                .andExpect(jsonPath("code").value(ErrorCode.NotFoundSpreaderEntityException.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.NotFoundSpreaderEntityException.getMessage()))
                .andExpect(jsonPath("detailMessage").exists())
        ;
    }


    @Test
    @DisplayName("조회 API 요건5 - 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.")
    void checkReadRequirement5() throws Exception {
        //given
        LocalDateTime pastExpireReadDate = LocalDateTime.now().minusDays(SpreaderConstant.EXPIRE_DAYS_OF_SPREAD);
        Spreader spreader = spreaderService.spread(_ROOM_ID, _USER_ID, 10000L, 3);
        spreader.setExpireReadDate(pastExpireReadDate);
        spreaderRepository.save(spreader);

        //when
        final ResultActions actions =
                mockMvc.perform(
                        get("/v1/spreader/" + spreader.getToken())
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));
        //then : 오류 발생
        actions
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("detailMessage").exists())
        ;
    }
}
