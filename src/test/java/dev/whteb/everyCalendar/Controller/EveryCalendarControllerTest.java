package dev.whteb.everyCalendar.Controller;


import dev.whteb.everyCalendar.DTO.GetIcsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class EveryCalendarControllerTest {

    @Test
    void 캘린더_URL이_잘못된_경우_예외를_던진다() {
        EveryCalendarController everyCalendarController = new EveryCalendarController(null);
        Assertions.assertThrows(Exception.class, () -> {
            everyCalendarController.getIcs(new GetIcsDTO("https://whiteb.dev/@3a123adkl1", new Date(), new Date()), null);
        });

    }
}