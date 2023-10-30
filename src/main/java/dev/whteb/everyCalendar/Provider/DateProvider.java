package dev.whteb.everyCalendar.Provider;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class DateProvider {
    public Date findNearestWeekDay(int weekDay, Date startDate) {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.setTime(startDate);
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, weekDay);

        if(cal.getTime().getTime() < startDate.getTime()) {
            cal.add(Calendar.WEEK_OF_MONTH, 1);
        }

        return cal.getTime();
    }
}
