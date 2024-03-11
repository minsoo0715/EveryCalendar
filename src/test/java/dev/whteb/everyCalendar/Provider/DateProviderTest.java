package dev.whteb.everyCalendar.Provider;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class DateProviderTest {

    Date base = getSpecificDate(2024, Calendar.JANUARY, 5);
    DateProvider dateProvider = new DateProvider();

    @Test
    void 특정_날짜를_반환한다() {
        int year = 2024;
        int month = 10;
        int day = 24;

        Date date = dateProvider.getDate(year, month, day);
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.YEAR)).isEqualTo(year);
        assertThat(calendar.get(Calendar.MONTH)).isEqualTo(month-1);
        assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(day);

    }

    @Test
    void 특정_요일_가장_가까운_날짜를_반환한다() {
        Date thursday = dateProvider.findNearestWeekDay(Calendar.THURSDAY, base);
        System.out.println("thursday = " + thursday);
        assertThat(thursday).isEqualTo(getSpecificDate(2024, Calendar.JANUARY, 11));
    }

    @Test
    void 시작일은_기간에_포함된다() {
        Date friday = dateProvider.findNearestWeekDay(Calendar.FRIDAY, base);
        System.out.println("friday = " + friday);
        assertThat(friday.getTime()).isEqualTo(base.getTime());
    }

    static Date getSpecificDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.setTimeInMillis(0);
        calendar.set(year, month, date, 0, 0, 0);
        return new Date(calendar.getTimeInMillis());
    }

}