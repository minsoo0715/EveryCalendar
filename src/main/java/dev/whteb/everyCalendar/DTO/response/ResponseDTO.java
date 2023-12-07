package dev.whteb.everyCalendar.DTO.response;

import dev.whteb.everyCalendar.DTO.EventDTO;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
@XmlRootElement(name="response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseDTO {
    @XmlElement(name="table")
    private TableDTO table;

    public List<EventDTO> convertToEvents() {
        ArrayList<EventDTO> lectures = new ArrayList<>();

       table.getSubjects().stream()
            .filter(s -> s.getTime().getData() != null)
            .forEach(s ->
                s.getTime().getData().forEach(data ->
                    lectures.add(data.toEventDTO(s))
                )
            );

       return lectures;
    }
}







