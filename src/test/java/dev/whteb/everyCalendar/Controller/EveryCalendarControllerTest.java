package dev.whteb.everyCalendar.Controller;


import dev.whteb.everyCalendar.DTO.GetIcsDTO;
import dev.whteb.everyCalendar.Provider.DateProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class EveryCalendarControllerTest {
    EveryCalendarController everyCalendarController = new EveryCalendarController(null, new DateProvider());

    @Test
    void 캘린더_URL이_잘못된_경우_예외를_던진다() {
        Assertions.assertThrows(Exception.class, () -> {
            everyCalendarController.getIcs(new GetIcsDTO("https://whiteb.dev/@3a123adkl1", new Date(), new Date()), null);
        });

    }
}