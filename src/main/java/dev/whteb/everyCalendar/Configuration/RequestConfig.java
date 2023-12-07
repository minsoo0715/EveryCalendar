package dev.whteb.everyCalendar.Configuration;

import dev.whteb.everyCalendar.DTO.response.ResponseDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Unmarshaller unmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ResponseDTO.class);
        return jaxbContext.createUnmarshaller();
    }
}
