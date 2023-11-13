package dev.whteb.everyCalendar.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Configuration
public class RequestConfig {

    @Bean(name="mockHttpHeaders")
    public HttpHeaders mockHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(
                Map.of(
                        HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8",
                        HttpHeaders.CONNECTION, "keep-alive",
                        HttpHeaders.PRAGMA, "no-cache",
                        HttpHeaders.HOST, "api.everytime.kr",
                        HttpHeaders.ORIGIN, "https://everytime.kr",
                        HttpHeaders.REFERER, "https://everytime.kr",
                        HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36"
                )
        );

        return headers;
    }
}
