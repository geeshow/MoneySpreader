package com.geeshow.kakaopay.MoneySpreader.utils.date;

import java.time.LocalDate;
import java.util.Calendar;

public class SpreaderDateUtils {

    /**
     * 금일 날짜 정보 조회
     * @return 금일 날짜(YYYY-MM-DD String).
     */
    public static String getToday() {
        return parseToDateString(LocalDate.now());
    }

    /**
     * 두 날짜의 일수 차이를 계산.
     * 한편으로 계산 함. (ex. from 20190202, to 20190203 -> result 1)
     * from/to 날짜가 같을 경우 0을 리턴 함.
     * @param from
     * @param to
     * @return 두 날짜의 일수 차이. 음수 포함.
     */
    public static int getNumberOfDays(String from, String to) {
        int yearOfFrom = Integer.parseInt(from.substring(0,4));
        int monthOfFrom = Integer.parseInt(from.substring(4,6)) - 1;
        int dayOfFrom = Integer.parseInt(from.substring(6,8));

        int yearOfTo = Integer.parseInt(to.substring(0,4));
        int monthOfTo = Integer.parseInt(to.substring(4,6)) - 1;
        int dayOfTo = Integer.parseInt(to.substring(6,8));

        Calendar calendar = Calendar.getInstance();
        calendar.set(yearOfFrom, monthOfFrom, dayOfFrom);
        long timeInMillisFrom = calendar.getTimeInMillis();

        calendar.set(yearOfTo, monthOfTo, dayOfTo);
        long timeInMillisTo = calendar.getTimeInMillis();
        Double result = Math.ceil((timeInMillisTo - timeInMillisFrom) / (24 * 60 * 60 * 1000));
        return result.intValue();
    }

    /**
     * 두 날짜의 일수 차이를 계산.
     * 양편으로 계산 함. (ex. from 20190202, to 20190203 -> result 2)
     * from/to 날짜가 같을 경우 1을 리턴 함.
     * @param from
     * @param to
     * @return 두 날짜의 일수 차이. 결과 > 0. 결과는 절대값으로 음수와 0은 포함 안됨.
     */
    public static int getNumberOfDaysClosed(String from, String to) {
        int numberOfDays = Math.abs(getNumberOfDays(from, to));
        return numberOfDays + 1;
    }

    /**
     * LocalDate의 날짜를 String으로 변환
     * @param localDate
     * @return 날짜형식의 String
     */
    public static String parseToDateString(LocalDate localDate) {
        int yearValue = localDate.getYear();
        int monthValue = localDate.getMonthValue();
        int dayValue = localDate.getDayOfMonth();

        StringBuilder buf = new StringBuilder(8);
        buf.append(yearValue);
        if ( monthValue < 10 ) {
            buf.append("0");
        }
        buf.append(monthValue);
        if ( dayValue < 10 ) {
            buf.append("0");
        }
        buf.append(dayValue);

        return buf.toString();
    }
}
