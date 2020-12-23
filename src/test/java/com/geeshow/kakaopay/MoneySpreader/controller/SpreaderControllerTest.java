package com.geeshow.kakaopay.MoneySpreader.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geeshow.kakaopay.MoneySpreader.domain.KakaoUser;
import com.geeshow.kakaopay.MoneySpreader.domain.RoomUser;
import com.geeshow.kakaopay.MoneySpreader.repository.KakaoUserRepository;
import com.geeshow.kakaopay.MoneySpreader.repository.RoomUserRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.RequestPost;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
class SpreaderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KakaoUserRepository kakaoUserRepository;
    @Autowired
    private RoomUserRepository roomUserRepository;

    private long _USER_ID;
    private String _ROOM_ID = "X-ROOM-ID-10";

    @BeforeEach
    void setUp() {

        // 뿌리기 사용자 등록
        KakaoUser spreader = kakaoUserRepository.save(KakaoUser.builder().balance(1000000L).build());
        roomUserRepository.save(RoomUser.builder().kakaoUser(spreader).roomId(_ROOM_ID).build());

        // 받는 사용자 등록
        IntStream.range(0, 3).forEach(num -> {
            roomUserRepository.save(RoomUser.builder().kakaoUser(
                    kakaoUserRepository.save(KakaoUser.builder().balance(0L).build())
            ).roomId(_ROOM_ID).build());
        });

        _USER_ID = spreader.getId();
    }

    @Test
    @DisplayName("뿌리기 등록 테스트")
    void spreadConCheckToken() throws Exception {
        // Given
        RequestPost requestPost = RequestPost.builder()
                .amount(20000)
                .number(3)
                .build();

        // When
        final ResultActions actions =
                mockMvc.perform(
                        post("/v1/spreader")
                                .header("X-USER-ID", _USER_ID)
                                .header("X-ROOM-ID", _ROOM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .content(objectMapper.writeValueAsString(requestPost)));
        // Then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("token").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.read").exists())
//                .andExpect(jsonPath("_links.receiver").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(
                        document(
                                "spreader",
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("url of self page"),
//                                        linkWithRel("reveiver").description("url of receipt"),
                                        linkWithRel("read").description("url of the spread information for spreader"),
                                        linkWithRel("profile").description("profile url")),
                                requestHeaders(
                                        headerWithName(HttpHeaders.ACCEPT).description("Accept header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type header"),
                                        headerWithName("X-USER-ID").description("User ID"),
                                        headerWithName("X-ROOM-ID").description("Room ID")),
                                requestFields(
                                        fieldWithPath("amount").description("Spread Amount"),
                                        fieldWithPath("number").description("Number of Receivers ")),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-type header")),
                                relaxedResponseFields(fieldWithPath("token").description("spread token"))));
    }
}