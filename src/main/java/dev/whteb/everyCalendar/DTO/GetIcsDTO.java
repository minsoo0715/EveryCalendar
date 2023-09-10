package dev.whteb.everyCalendar.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@Data
public class GetIcsDTO {
    String calendarUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date from;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date to;
}
