package dev.whteb.everyCalendar.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventDTO {
    String name;
    Long startTime;
    Long endTime;
    Integer weekDay;
    String place;
}
