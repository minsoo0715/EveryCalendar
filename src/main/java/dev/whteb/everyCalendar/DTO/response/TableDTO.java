package dev.whteb.everyCalendar.DTO.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class TableDTO {
    @XmlElement(name="subject")
    private ArrayList<SubjectDTO> subjects;
}
