package com.geeshow.kakaopay.MoneySpreader.requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto;
import com.geeshow.kakaopay.MoneySpreader.exception.entity.NotFoundUserInRoomEntityException;
import com.geeshow.kakaopay.MoneySpreader.exception.handler.ErrorCode;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.AlreadyReceivedTicketException;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.ExpiredTicketReceiptException;
import com.geeshow.kakaopay.MoneySpreader.exception.invalid.ReceiveOwnTicketException;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.config.docs-host}")
@ActiveProfiles("test")
public class ReceiptApiTest {
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
    private String _ROOM_ID = "X-ROOM-ID-92";

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
    @DisplayName("받기 API 요건1 - 뿌리기 시 발급된 token을 요청값으로 받습니다.")
    void checkReceiptRequirement1() throws Exception {
        //given
        long amount = 40000000;
        int ticketCount = 3;
        SpreaderDto.RequestSpreadDto requestSpreadDto = SpreaderDto.RequestSpreadDto.builder()
                .amount(amount)
                .number(ticketCount)
                .build();

        String responseAsString = mockMvc.perform(
                post("/v1/spreader")
                        .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                        .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestSpreadDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readValue(responseAsString, SpreaderDto.ResponseSpreadDto.class).getToken();

        //when : 토큰을 넘기지 않고 받기 API를 호출
        final ResultActions actions =
                mockMvc.perform(
                        put("/v1/spreader/receipt/")
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _RECEIVER_USER_ID1)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
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
    @DisplayName("받기 API 요건2 - token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를\n" +
            "API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다.")
    void checkReceiptRequirement2() throws Exception {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        put("/v1/spreader/receipt/{token}", token)
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _RECEIVER_USER_ID1)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("amount").isNumber())
        ;
    }

    @Test
    @DisplayName("받기 API 요건3 - 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.")
    void checkReceiptRequirement3() throws Exception {
        //given
        long amount = 10000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when : 사용자1이 받는다.
        spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, token);

        //then : 사용자1이 다시 받기를 시도하면서 오류 발생.
        AlreadyReceivedTicketException exception = assertThrows(AlreadyReceivedTicketException.class
                , ()-> spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, token));

        assertThat(exception.getMessage()).contains(ErrorCode.AlreadyReceivedTicketException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.AlreadyReceivedTicketException.getCode());
    }

    @Test
    @DisplayName("받기 API 요건4 - 자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    public void checkReceiptRequirement4() {
        //given
        long amount = 10000;
        int ticketCount = 3;

        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

        //when & then
        ReceiveOwnTicketException exception = assertThrows(ReceiveOwnTicketException.class
                , ()-> spreaderService.receive(_ROOM_ID, _USER_ID, token));

        assertThat(exception.getMessage()).contains(ErrorCode.ReceiveOwnTicketException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.ReceiveOwnTicketException.getCode());
    }


    @Test
    @DisplayName("받기 API 요건5 - 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.")
    public void checkReceiptRequirement5() {
        //given

        // 뿌리기 사용자 등록 후 대화방에 등록
        String anotherRoom = "ANOTHER_ROOM_10";

        // 새로운 대화방에 사용자 2명 등록
        KakaoUser anotherSpreader = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(anotherSpreader).roomId(anotherRoom).build());
        KakaoUser anotherReceiver = kakaoUserRepository.save(KakaoUser.builder().balance(0L).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(anotherReceiver).roomId(anotherRoom).build());

        // 뿌리기 실행
        String token = spreaderService.spread(anotherRoom, anotherSpreader.getId(), 10000, 1).getToken();

        //when & then : 다른방 사용자가 ANOTHER_ROOM_10 방의 번호와 토큰으로 수취를 시도하여 오류.
        NotFoundUserInRoomEntityException exception = assertThrows(NotFoundUserInRoomEntityException.class
                , ()-> spreaderService.receive(anotherRoom, _RECEIVER_USER_ID1, token));

        assertThat(exception.getMessage()).contains(ErrorCode.NotFoundUserInRoomEntityException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.NotFoundUserInRoomEntityException.getCode());
    }


    @Test
    @DisplayName("받기 API 요건6 - 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.")
    public void checkReceiptRequirement6() {
        //given
        LocalDateTime pastExpireReceiptDate = LocalDateTime.now().minusMinutes(SpreaderConstant.EXPIRE_MINUTES_OF_RECEIPT);

        // 받기 가능한 날짜를 과거 설정해서 뿌리기 등록
        Spreader spreader = spreaderService.spread(_ROOM_ID, _USER_ID, 1000, 3);
        spreader.setExpireReceiptDate(pastExpireReceiptDate);
        spreaderRepository.save(spreader);

        //when & then
        ExpiredTicketReceiptException exception = assertThrows(ExpiredTicketReceiptException.class
                , ()-> spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, spreader.getToken()));

        assertThat(exception.getMessage()).contains(ErrorCode.ExpiredTicketReceiptException.getMessage());
        assertThat(exception.getCode()).isEqualTo(ErrorCode.ExpiredTicketReceiptException.getCode());

    }
}
