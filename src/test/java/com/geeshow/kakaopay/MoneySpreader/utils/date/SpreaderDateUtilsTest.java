package com.geeshow.kakaopay.MoneySpreader.utils.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class SpreaderDateUtilsTest {

    @Test
    @DisplayName("날짜 유틸 - 두 날짜간 일수 구하기")
    public void getNumberOfDays() throws Exception {
        //given

        //when
        int result1 = SpreaderDateUtils.getNumberOfDays("20190101", "20190101");
        int result2 = SpreaderDateUtils.getNumberOfDays("20190101", "20190102");
        int result3 = SpreaderDateUtils.getNumberOfDays("20190331", "20190401");
        int result4 = SpreaderDateUtils.getNumberOfDays("20190102", "20190101");
        int result5 = SpreaderDateUtils.getNumberOfDays("20190401", "20190331");
        int result6 = SpreaderDateUtils.getNumberOfDays("20190401", "20200401");
        int result7 = SpreaderDateUtils.getNumberOfDays("20200401", "20210401");
        int result8 = SpreaderDateUtils.getNumberOfDays("20200401", "20200501");
        int result9 = SpreaderDateUtils.getNumberOfDaysClosed("20200501", "20200601");
        int result10 = SpreaderDateUtils.getNumberOfDaysClosed("20200201", "20200301");
        int result11 = SpreaderDateUtils.getNumberOfDaysClosed("20190201", "20190301");

        //then
        assertThat(result1).isEqualTo(0);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(1);
        assertThat(result4).isEqualTo(-1);
        assertThat(result5).isEqualTo(-1);
        assertThat(result6).isEqualTo(366);
        assertThat(result7).isEqualTo(365);
        assertThat(result8).isEqualTo(30);
        assertThat(result9).isEqualTo(32);
        assertThat(result10).isEqualTo(30);
        assertThat(result11).isEqualTo(29);
    }

    @Test
    @DisplayName("날짜 유틸 - LocalDate to String 형변환")
    public void parseToDateString() throws Exception {
        //given
        LocalDate theDate = LocalDate.of(2020, 12, 23);

        //when
        String dateString = SpreaderDateUtils.parseToDateString(theDate);

        //then
        assertThat(dateString).isEqualTo("20201223");
    }
}