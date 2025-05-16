package ru.linedown.nefeslechat.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class DaySchedule {
    private String dayOfWeek;
    private Date date;
    private List<Lesson> lessons = new ArrayList<>();

    public DaySchedule(String dayOfWeek, Date date) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }
}
