package dev.whteb.everyCalendar.Service;

import dev.whteb.everyCalendar.Configuration.RequestConfig;
import dev.whteb.everyCalendar.DTO.response.ResponseDTO;
import dev.whteb.everyCalendar.Provider.DateProvider;
import jakarta.xml.bind.Unmarshaller;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;


import static org.assertj.core.api.Assertions.assertThat;

class EveryCalendarServiceTest {

    private static EveryCalendarService everyCalendarService;
    private static HttpHeaders mockHttpHeaders;

    @BeforeAll
    static void beforeAll() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(RequestConfig.class);
        RestTemplate restTemplate = ac.getBean("restTemplate", RestTemplate.class);
        mockHttpHeaders = ac.getBean("mockHttpHeaders", HttpHeaders.class);
        Unmarshaller unmarshaller = ac.getBean("unmarshaller", Unmarshaller.class);

        everyCalendarService = new EveryCalendarService(new DateProvider(), restTemplate, mockHttpHeaders, unmarshaller);
    }

    @Test
    void ICAL_생성_테스트() {
        File file = new File("./src/test/resources", "friend.xml");
        assertThat(file.exists()).isTrue();

        StringBuilder everything = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while( (line = br.readLine()) != null) {
                everything.append(line);
            }
        } catch(IOException ignored) {}

        System.out.println("everything = " + everything);

        ResponseDTO result = ReflectionTestUtils.invokeMethod(everyCalendarService, "parseTimetable", everything.toString());
        assertThat(result).isNotNull();

        System.out.println("result = " + result.convertToEvents());
        assertThat(result.convertToEvents().size()).isEqualTo(8);
    }

    @Test
    void 외부_API에_요청을_보내면_403이_발생하지_않는다() {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.everytime.kr")
                .path("/")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        Assertions.assertThatThrownBy(
                () -> restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>("", mockHttpHeaders), String.class))
                .isNotInstanceOf(HttpClientErrorException.Forbidden.class);
    }

}