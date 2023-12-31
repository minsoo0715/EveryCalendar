package dev.whteb.everyCalendar.DTO.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class SubjectDTO {

    @XmlElement
    private Property internal;

    @XmlElement
    private Property name;

    @XmlElement
    private Property professor;

    @XmlElement(name="time")
    private TimeDTO time;

}

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
class Property {
    @XmlAttribute
    String value;
}
