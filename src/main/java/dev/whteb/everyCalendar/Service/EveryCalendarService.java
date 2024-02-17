package dev.whteb.everyCalendar.Service;

import dev.whteb.everyCalendar.DTO.EventDTO;
import dev.whteb.everyCalendar.DTO.response.ResponseDTO;
import dev.whteb.everyCalendar.Provider.DateProvider;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EveryCalendarService {
    private final DateProvider dateProvider;
    private final RestTemplate restTemplate;
    private final HttpHeaders mockHttpHeaders;
    private final Unmarshaller unmarshaller;

    public String generateCalendar(String identifier, Date startDate, Date endDate) throws Exception {
        ResponseDTO response = requestTimetable(identifier);
        List<EventDTO> lectures = response.convertToEvents();

        Calendar lectureCalendar = createWeeklyCalendarBetween(lectures, startDate, endDate);
        return lectureCalendar.toString();
    }

    private Calendar createWeeklyCalendarBetween(List<EventDTO> events, Date startDate, Date endDate){
        Calendar calendar = new Calendar();

        events.forEach(l -> {
            Date nearestDay = dateProvider.findNearestWeekDay((l.getWeekDay() + 2) % 7, startDate);

            calendar.add(new VEvent()
                    .withProperty(new Summary(l.getName()))
                    .withProperty(new Location(l.getPlace()))
                    .withProperty(new DtStart<>(Instant.ofEpochMilli(nearestDay.getTime() + l.getStartTime()) ))
                    .withProperty(new DtEnd<>(Instant.ofEpochMilli(nearestDay.getTime() + l.getEndTime())))
                    .withProperty(new RRule<>("FREQ=WEEKLY;UNTIL=" + endDate.toInstant().plus(1, ChronoUnit.DAYS).toString().replaceAll("[:-]", "")))
                    .getFluentTarget()
            );
        });

        return calendar;
    }

    private ResponseDTO requestTimetable(String identifier) throws Exception {

        URI uri = UriComponentsBuilder
                .fromUriString("https://api.everytime.kr")
                .path("/find/timetable/table/friend")
                .queryParam("friendInfo", true)
                .queryParam("identifier", identifier)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>("", mockHttpHeaders), String.class);

        if (response.getStatusCode() != HttpStatus.OK) throw new Exception("서버가 응답하지 않습니다.");

        String body = response.getBody();
        ResponseDTO responseDTO = parseTimetable(body);

        if(responseDTO.getTable() == null) throw new Exception("시간표 URL이 올바르지 않습니다.");
        if(responseDTO.getTable().getSubjects() == null) throw new Exception("시간표가 공개되어 있지 않거나, 등록된 강의가 없습니다.");

        return responseDTO;
    }

    private ResponseDTO parseTimetable(String xml) throws JAXBException {
        return (ResponseDTO) unmarshaller.unmarshal(new StringReader(xml));
    }
}
