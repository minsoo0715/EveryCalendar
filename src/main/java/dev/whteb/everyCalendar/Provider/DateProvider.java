package dev.whteb.everyCalendar.Provider;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class DateProvider {
    public Date findNearestWeekDay(int weekDay, Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, weekDay);

        if(cal.getTimeInMillis() < startDate.getTime()) {
            cal.add(Calendar.WEEK_OF_MONTH, 1);
        }

        return cal.getTime();
    }

    public Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(0);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);


        return cal.getTime();
    }

    @PostConstruct
    private void initTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
