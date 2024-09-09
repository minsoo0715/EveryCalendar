package dev.whteb.everyCalendar.DTO.response;

import dev.whteb.everyCalendar.DTO.EventDTO;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeDTO {

    @Getter
    @Setter
    @ToString
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Data {

        @XmlAttribute
        Integer day;

        @XmlAttribute(name="starttime")
        Long startTime;

        @XmlAttribute(name="endtime")
        Long endTime;

        @XmlAttribute
        String place;

        EventDTO toEventDTO(SubjectDTO subject) {
            return EventDTO.builder()
                    .name(subject.getName().getValue())
                    .startTime(startTime * 5 * 60 * 1000L)
                    .endTime(endTime * 5 * 60 * 1000L)
                    .weekDay(day)
                    .place(place)
                    .build();
        }
    }

    @XmlElement
    private ArrayList<Data> data;
}
