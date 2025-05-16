package ru.linedown.nefeslechat.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Lesson {
    private String paraNumber;
    private Date startTime;
    private Date endTime;
    private String room;
    private String subject;
    private String teacher;
    private String lessonType;
}
