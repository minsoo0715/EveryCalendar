package dev.whteb.everyCalendar.Controller;

import dev.whteb.everyCalendar.DTO.GetIcsDTO;
import dev.whteb.everyCalendar.Service.EveryCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EveryCalendarController {
    private final EveryCalendarService everyCalendarService;

    @GetMapping
    String index() {
        return "index";
    }

    @PostMapping(value = "/")
    ResponseEntity<String> getIcs(@ModelAttribute GetIcsDTO getIcsDTO) throws Exception {

        String calendarUrl = getIcsDTO.getCalendarUrl();
        Pattern pattern = Pattern.compile("(?<=(https://everytime.kr/@))[A-Za-z0-9]{1,}");
        Matcher matcher = pattern.matcher(calendarUrl);

        matcher.find();
        String identifier = matcher.group();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "calendar.ics" +"\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar")
                .body(everyCalendarService.createIcsString(identifier, getIcsDTO.getFrom(), getIcsDTO.getTo()));
    }
}
