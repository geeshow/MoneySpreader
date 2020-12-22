package com.geeshow.kakaopay.MoneySpreader.common;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriHost = "${app.host}")
@ActiveProfiles("test")
class SpreaderUtilsTest {

    @Test
    @DisplayName("토큰 생성 테스트")
    void generateTokenText() throws Exception {
        String token = SpreaderUtils.generateToken(SpreaderConstant.TOKEN_SIZE);
        System.out.println(token);
        assertNotNull(token);
    }

}