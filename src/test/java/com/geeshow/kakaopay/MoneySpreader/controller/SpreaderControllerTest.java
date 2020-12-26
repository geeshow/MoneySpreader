package com.geeshow.kakaopay.MoneySpreader.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.RequestSpreadDto;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.config.docs-host}")
@ActiveProfiles("test")
class SpreaderControllerTest {

    @Autowired private MockMvc mockMvc;
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
    private String _ROOM_ID = "X-ROOM-ID-20";

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
    @DisplayName("뿌리기 등록 테스트")
    void spreadConCheckToken() throws Exception {
        //given
        RequestSpreadDto requestSpreadDto = RequestSpreadDto.builder()
                .amount(20000)
                .number(3)
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
                .andDo(
                        document(
                                "spreader",
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("현재 페이지의 URL"),
                                        linkWithRel("receipt").description("뿌린 티켓을 수취하기 위한 URL. 뿌린이는 수령 불가."),
                                        linkWithRel("read").description("뿌린 티켓의 수취 상태를 조회하기 위한 URL. 뿌린이만 조회 가능함."),
                                        linkWithRel("profile").description("profile url")),
                                requestHeaders(
                                        headers()
                                ),
                                requestFields(
                                        fieldWithPath("amount").description("뿌리기 금액"),
                                        fieldWithPath("number").description("받을 대상 맴버수")),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type header")),
                                relaxedResponseFields(fieldWithPath("token").description("뿌리기 token"))));
    }

    @Test
    @DisplayName("뿌리기 조회 테스트")
    void spreadReadTest() throws Exception {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();
        SpreaderTicket receive = spreaderService.receive(_ROOM_ID, _RECEIVER_USER_ID1, token);

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
                .andExpect(jsonPath("receiptAmount").value(receive.getAmount()))
                .andExpect(jsonPath("receipts[0].userId").value(receive.getReceiverUserId()))
                .andExpect(jsonPath("receipts[0].amount").value(receive.getAmount()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.spreader").exists())
                .andExpect(jsonPath("_links.receipt").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(
                        document(
                                "read",
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("현재 페이지의 URL"),
                                        linkWithRel("spreader").description("대화방 사용자에게 지정한 금액을 랜덤 분산하여 뿌리기 URL"),
                                        linkWithRel("receipt").description("뿌린 티켓을 수취하기 위한 URL. 뿌린이는 수령 불가."),
                                        linkWithRel("profile").description("profile url")),
                                requestHeaders(headers()),
                                pathParameters(parameterWithName("token").description("Spread token")),
                                responseHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type header")),
                                relaxedResponseFields(
                                        fieldWithPath("spreadDatetime").description("뿌린 시각"),
                                        fieldWithPath("spreadAmount").description("뿌린 금액"),
                                        fieldWithPath("receiptAmount").description("현재까지 받은 금액"),
                                        fieldWithPath("receipts[].userId").description("받은 사용자 ID"),
                                        fieldWithPath("receipts[].amount").description("받은 금액")
                                )
                        )
                );
    }


    @Test
    @DisplayName("받기를 요청하고 받은 금액을 반환 받음")
    void receiveTest() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("amount").isNumber())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.spreader").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(
                        document(
                                "receipt",
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("현재 페이지의 URL"),
                                        linkWithRel("spreader").description("대화방 사용자에게 지정한 금액을 랜덤 분산하여 뿌리기 URL"),
                                        linkWithRel("profile").description("profile url")),
                                requestHeaders(headers()),
                                pathParameters(parameterWithName("token").description("뿌리기 token")),
                                responseHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type 헤더")),
                                relaxedResponseFields(fieldWithPath("amount").description("받은 금액"))));
    }

    @Test
    @DisplayName("뿌리기 필수값 입력 오류 테스트.")
    void spreaderErrorTest() throws Exception {
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
                .andExpect(jsonPath("status").value(ErrorCode.MethodArgumentNotValidException.getStatus().value()))
                .andExpect(jsonPath("message").value(ErrorCode.MethodArgumentNotValidException.getMessage()))
                .andExpect(jsonPath("code").value(ErrorCode.MethodArgumentNotValidException.getCode()))
                .andExpect(jsonPath("detailMessage").exists())
                .andExpect(jsonPath("detailErrors").exists())
                .andExpect(jsonPath("detailErrors[0].object").value("requestSpreadDto"))
                .andExpect(jsonPath("detailErrors[0].field").value("number"))
                .andExpect(jsonPath("detailErrors[0].rejectedValue").value(0))
                .andExpect(jsonPath("detailErrors[0].message").exists())
                .andDo(document("errors",
                        responseFields(
                                fieldWithPath("timestamp").description("오류 발생 시간"),
                                fieldWithPath("status").description("ERROR HTTP STATUS CODE"),
                                fieldWithPath("message").description("기본 오류 메시지"),
                                fieldWithPath("code").description("업무 오류 코드"),
                                fieldWithPath("detailMessage").description("Stack trace")
                        )
                        .and(subsectionWithPath("detailErrors").type(JsonFieldType.ARRAY).description("오류 상세 메시지(해당 시)"),
                            subsectionWithPath("detailErrors[].object").type(JsonFieldType.STRING).description("오류 발생 객체"),
                            subsectionWithPath("detailErrors[].field").type(JsonFieldType.STRING).description("오류 필드"),
                            subsectionWithPath("detailErrors[].rejectedValue").type(JsonFieldType.NUMBER).description("오류 발생 값"),
                            subsectionWithPath("detailErrors[].message").type(JsonFieldType.STRING).description("오류 메시지")
                        )
                ));
    }

    private List<HeaderDescriptor> headers() {
        return Arrays.asList(
                headerWithName(SpreaderConstant.HTTP_HEADER_USER_ID).description("사용자 ID"),
                headerWithName(SpreaderConstant.HTTP_HEADER_ROOM_ID).description("대화방 ID"));
    }


    @Test
    @DisplayName("뿌리기 등록 오류 테스트 - 헤더값(X_ROOM_ID) 누락")
    void spreadConCheckTokenWithoutHeader() throws Exception {
        //given
        RequestSpreadDto requestSpreadDto = RequestSpreadDto.builder()
                .amount(20000)
                .number(3)
                .build();

        //when
        final ResultActions actions =
                mockMvc.perform(
                        post("/v1/spreader")
                                .header(SpreaderConstant.HTTP_HEADER_USER_ID, _USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(requestSpreadDto)));
        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(ErrorCode.MissingRequestHeaderException.getStatus().value()))
                .andExpect(jsonPath("code").value(ErrorCode.MissingRequestHeaderException.getCode()))
                .andExpect(jsonPath("message").value(ErrorCode.MissingRequestHeaderException.getMessage()))
                .andExpect(jsonPath("detailMessage").exists())
        ;

    }


    @Test
    @DisplayName("뿌리기 조회 테스트 - 수령인 없음")
    void spreadReadTestWithoutReceiver() throws Exception {
        //given
        long amount = 10000;
        int ticketCount = 3;
        String token = spreaderService.spread(_ROOM_ID, _USER_ID, amount, ticketCount).getToken();

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
                .andExpect(jsonPath("receiptAmount").value(0))
                .andExpect(jsonPath("receipts[0]").doesNotExist())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.spreader").exists())
                .andExpect(jsonPath("_links.receipt").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }

}