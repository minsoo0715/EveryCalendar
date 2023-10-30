package dev.whteb.everyCalendar.Controller;

import dev.whteb.everyCalendar.DTO.GetIcsDTO;
import dev.whteb.everyCalendar.Service.EveryCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping(value = "/calendar")
    String getIcs(@ModelAttribute GetIcsDTO getIcsDTO, RedirectAttributes redirectAttributes) throws Exception {

        String calendarUrl = getIcsDTO.getCalendarUrl();
        Pattern pattern = Pattern.compile("(?<=(https://everytime.kr/@))[A-Za-z0-9]{1,}");
        Matcher matcher = pattern.matcher(calendarUrl);

        if(!matcher.find()) {
            throw new Exception("시간표 URL이 올바르지 않습니다.");
        }

        String identifier = matcher.group();

        String ics = everyCalendarService.createIcsString(identifier, getIcsDTO.getFrom(), getIcsDTO.getTo());
        redirectAttributes.addFlashAttribute("ics", ics);

        return "redirect:/success";
    }

    @GetMapping("/success")
    ResponseEntity<String> success(@ModelAttribute("ics") String ics) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "calendar.ics" +"\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar")
                .body(ics);
    }

}
