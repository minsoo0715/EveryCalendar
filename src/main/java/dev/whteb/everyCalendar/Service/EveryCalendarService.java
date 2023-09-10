package dev.whteb.everyCalendar.Service;

import dev.whteb.everyCalendar.DTO.EventDTO;
import dev.whteb.everyCalendar.Provider.DateProvider;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EveryCalendarService {
    private final DateProvider dateProvider;
    private final DocumentBuilder xmlParser;
    private HttpURLConnection getConn(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Pragma", "no-cache");
        conn.setRequestProperty("Host", "api.everytime.kr");
        conn.setRequestProperty("Origin", "https://everytime.kr");
        conn.setRequestProperty("Referer", "https://everytime.kr");
        conn.setRequestProperty("User-Agent",  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        return conn;
    }

    public String createIcsString(String identifier, Date startDate, Date endDate) throws Exception {
        Calendar calendar = new Calendar();

        String xml = getXmlFromEverytime(identifier);
        InputSource source = new InputSource(new StringReader(xml));
        Document doc = xmlParser.parse(source);
        Element root = doc.getDocumentElement();

        List<EventDTO> lectures = extractData(root);

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

    private String getXmlFromEverytime(String identifier) throws Exception {
        HttpURLConnection conn = getConn("https://api.everytime.kr/find/timetable/table/friend?friendInfo=true&identifier=" + identifier, "POST");

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new Exception();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private List<EventDTO> extractData(Element root) {

        ArrayList<EventDTO> lectures = new ArrayList<>();

        NodeList nList = root.getElementsByTagName("subject");
        for(int i = 0; i<nList.getLength(); ++i) {
            Element element = (Element) nList.item(i);
            String name =  getAttributeValue(element.getElementsByTagName("name").item(0), "value");

            NodeList dataList = element.getElementsByTagName("data");
            for(int j = 0; j < dataList.getLength(); ++j) {
                Node data = dataList.item(j);

                lectures.add(
                    EventDTO.builder()
                            .name(name)
                            .startTime(Integer.parseInt(getAttributeValue(data, "starttime")) * 5 * 60 * 1000L)
                            .endTime(Integer.parseInt(getAttributeValue(data, "endtime")) * 5 * 60 * 1000L)
                            .weekDay(Integer.parseInt(getAttributeValue(data, "day")))
                            .place(getAttributeValue(data, "place"))
                            .build()
                );
            }
        }


        return lectures;
    }

    private String getAttributeValue(Node node, String name) {
        NamedNodeMap attributes = node.getAttributes();
        return attributes.getNamedItem(name).getNodeValue();
    }
}
