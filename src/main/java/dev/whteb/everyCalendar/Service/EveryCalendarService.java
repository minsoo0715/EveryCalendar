package dev.whteb.everyCalendar.Service;

import dev.whteb.everyCalendar.DTO.EventDTO;
import dev.whteb.everyCalendar.Provider.DateProvider;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EveryCalendarService {
    private final DateProvider dateProvider;
    private final DocumentBuilder xmlParser;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders mockHttpHeaders;

    public String createIcsString(String identifier, Date startDate, Date endDate) throws Exception {
        Calendar calendar = new Calendar();
        Element body = getXmlFromEverytime(identifier);

        List<EventDTO> lectures = extractData(body);

        lectures.forEach(l -> {
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

        return calendar.toString();
    }

    private Element getXmlFromEverytime(String identifier) throws Exception {

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

        InputSource source = new InputSource(new StringReader(response.getBody()));
        Document doc = xmlParser.parse(source);

        Element rootElement = doc.getDocumentElement();

        if(rootElement.getTextContent().equals("-1")) throw new Exception("시간표 URL이 올바르지 않습니다.");

        return rootElement;
    }

    private List<EventDTO> extractData(Element root) throws Exception {

        ArrayList<EventDTO> lectures = new ArrayList<>();

        NodeList subjects = root.getElementsByTagName("subject");

        if(subjects.getLength() == 0) throw new Exception("강의가 등록되어 있지 않거나 시간표가 비공개 되어있습니다.");

        for(int i = 0; i<subjects.getLength(); ++i) {
            Element element = (Element) subjects.item(i);
            String name =  getAttributeValue(element.getElementsByTagName("name").item(0), "value");

            NodeList dataList = element.getElementsByTagName("data");

            IntStream.range(0, dataList.getLength())
                    .mapToObj(dataList::item)
                    .forEach((data) ->
                            lectures.add(
                                    EventDTO.builder()
                                            .name(name)
                                            .startTime(Integer.parseInt(getAttributeValue(data, "starttime")) * 5 * 60 * 1000L)
                                            .endTime(Integer.parseInt(getAttributeValue(data, "endtime")) * 5 * 60 * 1000L)
                                            .weekDay(Integer.parseInt(getAttributeValue(data, "day")))
                                            .place(getAttributeValue(data, "place"))
                                            .build()
                            )
                    );

        }

        return lectures;
    }

    private String getAttributeValue(Node node, String name) {
        NamedNodeMap attributes = node.getAttributes();
        return attributes.getNamedItem(name).getNodeValue();
    }
}
