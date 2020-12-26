package com.geeshow.kakaopay.MoneySpreader.requirement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geeshow.kakaopay.MoneySpreader.constant.HttpErrorMessages;
import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.config.docs-host}")
@ActiveProfiles("test")
public class SpreaderApiTest {
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
    private String _ROOM_ID = "X-ROOM-ID-30";

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
    @DisplayName("뿌리기 API 요건1 - 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.")
    void checkRequirement1() throws Exception {
        //given
        SpreaderDto.RequestSpreadDto requestSpreadDto = SpreaderDto.RequestSpreadDto.builder()
                .amount(10000)
                .build();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        post("/v1/spreader")
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _RECEIVER_USER_ID1)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(requestSpreadDto)));
        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value(HttpErrorMessages.INVALID_BODY_DATA))
                .andExpect(jsonPath("detailMessage").exists())
                .andExpect(jsonPath("detailErrors[0].field").value("number"))
        ;
    }

    @Test
    @DisplayName("뿌리기 API 요건2 - 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.")
    void checkRequirement2() throws Exception {
        //given
        SpreaderDto.RequestSpreadDto requestSpreadDto = SpreaderDto.RequestSpreadDto.builder()
                .amount(40000)
                .number(1)
                .build();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        post("/v1/spreader")
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                                .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(requestSpreadDto)));
        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("token").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.read").exists())
                .andExpect(jsonPath("_links.receipt").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }

    @Test
    @DisplayName("뿌리기 API 요건3 - 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다.")
    void checkRequirement3() throws Exception {
        //given
        long amount = 40000000;
        int ticketCount = 3;
        SpreaderDto.RequestSpreadDto requestSpreadDto = SpreaderDto.RequestSpreadDto.builder()
                .amount(amount)
                .number(ticketCount)
                .build();

        //when
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

        Spreader spreader = spreaderRepository.findByToken(token)
                .orElseThrow(() -> new AssertionError("뿌리기 조회 오류"));

        //then
        assertThat(spreader.getAmount()).isEqualTo(amount);
        assertThat(spreader.getSpreaderTickets().size()).isEqualTo(spreader.getTicketCount());
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(1).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(2).getAmount()).isGreaterThan(0);
        assertThat(spreader.getSpreaderTickets().get(0).isReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(1).isReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(2).isReceived()).isFalse();
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isNotEqualTo(spreader.getSpreaderTickets().get(1).getAmount());
        assertThat(spreader.getSpreaderTickets().get(0).getAmount()).isNotEqualTo(spreader.getSpreaderTickets().get(2).getAmount());
        assertThat(spreader.getSpreaderTickets().get(1).getAmount()).isNotEqualTo(spreader.getSpreaderTickets().get(2).getAmount());
    }

    @Test
    @DisplayName("뿌리기 API 요건4 - token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다.")
    void checkRequirement4() throws Exception {
        //given
        long amount = 40000;
        int ticketCount = 3;
        SpreaderDto.RequestSpreadDto requestSpreadDto = SpreaderDto.RequestSpreadDto.builder()
                .amount(amount)
                .number(ticketCount)
                .build();

        //when
        String responseAsString1 = mockMvc.perform(
                post("/v1/spreader")
                        .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                        .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestSpreadDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String responseAsString2 = mockMvc.perform(
                post("/v1/spreader")
                        .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                        .header(SpreaderConstant.HTTP_HEADER_ROOM_ID, _ROOM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestSpreadDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();


        //then
        String token1 = objectMapper.readValue(responseAsString1, SpreaderDto.ResponseSpreadDto.class).getToken();
        String token2 = objectMapper.readValue(responseAsString2, SpreaderDto.ResponseSpreadDto.class).getToken();
        assertThat(token1).isNotEqualTo(token2);
    }
}
