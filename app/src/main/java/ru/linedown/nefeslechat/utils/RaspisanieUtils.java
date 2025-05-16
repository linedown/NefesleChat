package ru.linedown.nefeslechat.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.linedown.nefeslechat.entity.DaySchedule;
import ru.linedown.nefeslechat.entity.Lesson;

public class RaspisanieUtils {
    private static final String BASE_URL = "https://rasp.pgups.ru/schedule/group/";

    private static final String SEARCH_URL_FIRST_PART = "https://rasp.pgups.ru/search?title=";
    private static final String SEARCH_URL_SECOND_PART = "&by=";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static String title = "";
    public static String by = "";

    public static void parser(){
        try {
            List<DaySchedule> schedule = parseSchedulePage();

            if (schedule != null) {
                for (DaySchedule day : schedule) {
                    Log.d("Расписание", "День недели: " + day.getDayOfWeek() + " (" + day.getDate() + ")");
                    for (Lesson lesson : day.getLessons()) {
                        Log.d("Расписание","  Пара: " + lesson.getParaNumber());
                        if (lesson.getStartTime() != null && lesson.getEndTime() != null) {
                            Log.d("Расписание","  Время: " + lesson.getStartTime() + " - " + lesson.getEndTime());
                        } else {
                            Log.i("Расписание","  Время: Не определено");
                        }
                        Log.d("Расписание","  Кабинет: " + lesson.getRoom());
                        Log.d("Расписание","  Предмет: " + lesson.getSubject());
                        Log.d("Расписание","  Преподаватель: " + lesson.getTeacher());
                        Log.d("Расписание","  Тип занятия: " + lesson.getLessonType());
                    }
                }
            } else {
                Log.w("Расписание", "Не удалось получить расписание");
            }

        } catch (IOException e) {
            Log.e("Расписание", "Ошибка" + e.getMessage());
        }
    }

    private static List<DaySchedule> parseSchedulePage() throws IOException {
        List<DaySchedule> schedule = new ArrayList<>();
        boolean isOddWeek = isOddWeek();

        try {
            Document searchDoc = Jsoup.connect(SEARCH_URL_FIRST_PART + title + SEARCH_URL_SECOND_PART + by).get();
            Elements groupLinks = searchDoc.select("#kt_content > div.kt-container.kt-grid__item.kt-grid__item--fluid > div:nth-child(2) > div.kt-portlet__body > div > div > div > a");

            if (groupLinks.isEmpty()) {
                Log.w("Расписание","Не найдена ссылка на группу.");
                return null;
            }

            String relativeScheduleUrl = groupLinks.first().attr("href");
            String groupId = relativeScheduleUrl.substring(relativeScheduleUrl.lastIndexOf("/") + 1);
            String scheduleUrl = BASE_URL + groupId + "?odd=" + (isOddWeek ? "1" : "0");

            Document doc = Jsoup.connect(scheduleUrl).get();
            Elements dayTables = doc.select("div.kt-portlet__body > table.table.m-table.mb-5");

            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

            for (int i = 0; i < dayTables.size(); i++) {
                Element table = dayTables.get(i);
                LocalDate currentDate = monday.plusDays(i);
                DayOfWeek dayOfWeekEnum = currentDate.getDayOfWeek();
                java.sql.Date date = java.sql.Date.valueOf(String.valueOf(currentDate));

                String dayOfWeek = table.previousElementSibling() != null ? table.previousElementSibling().text() : table.select("td.d-none.d-md-table-cell h4").text().replace(" ", "").replace("\n", "");

                if (dayOfWeek.isEmpty()) {
                    continue;
                }
                DaySchedule daySchedule = new DaySchedule(dayOfWeek, date);

                Elements lessons = table.select("tbody > tr");

                for (Element lessonRow : lessons) {
                    Lesson lesson = new Lesson();

                    Elements paraNumberCell = lessonRow.select("td.d-none.d-md-table-cell div[style*='font-weight: 400']");
                    if (!paraNumberCell.isEmpty()) {
                        lesson.setParaNumber(paraNumberCell.first().text().replace("пара", "").trim());
                    }

                    Elements timeCell = lessonRow.select("td[width='15%'] div span");
                    if (!timeCell.isEmpty()) {
                        String timeString = timeCell.first().text();
                        String[] times = timeString.split("—");
                        if (times.length == 2) {
                            String startTimeString = times[0].trim();
                            String endTimeString = times[1].trim();
                            try {
                                LocalTime startTimeLocal = LocalTime.parse(startTimeString, TIME_FORMATTER);
                                LocalTime endTimeLocal = LocalTime.parse(endTimeString, TIME_FORMATTER);

                                // Объединяем дату и время с использованием Calendar
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date); // Устанавливаем дату
                                calendar.set(Calendar.HOUR_OF_DAY, startTimeLocal.getHour());
                                calendar.set(Calendar.MINUTE, startTimeLocal.getMinute());
                                calendar.set(Calendar.SECOND, 0);
                                Date startTime = calendar.getTime();

                                calendar.setTime(date); // Сбрасываем Calendar к той же дате
                                calendar.set(Calendar.HOUR_OF_DAY, endTimeLocal.getHour());
                                calendar.set(Calendar.MINUTE, endTimeLocal.getMinute());
                                calendar.set(Calendar.SECOND, 0);
                                Date endTime = calendar.getTime();

                                lesson.setStartTime(startTime);
                                lesson.setEndTime(endTime);

                            } catch (java.time.format.DateTimeParseException e) {
                                Log.e("Расписание","Ошибка при парсинге времени: " + timeString);
                                lesson.setStartTime(null);
                                lesson.setEndTime(null);
                            }
                        } else {
                            Log.e("Расписание","Неправильный формат времени: " + timeString);
                            lesson.setStartTime(null);
                            lesson.setEndTime(null);
                        }
                    }

                    Elements roomCell = lessonRow.select("td[width='15%'] div.text-center.mt-2 a");
                    if (!roomCell.isEmpty()) {
                        lesson.setRoom(roomCell.first().text().replace("\u00A0", " "));
                    }

                    Elements subjectCell = lessonRow.select("td.align-middle div.mb-2 span:first-child");
                    if (!subjectCell.isEmpty()) {
                        lesson.setSubject(subjectCell.first().text());
                    }

                    Elements lessonTypeCell = lessonRow.select("td.align-middle div.mb-2 span.badge");
                    if (!lessonTypeCell.isEmpty()) {
                        lesson.setLessonType(lessonTypeCell.first().text());
                    }

                    String teacherName = null;
                    Elements teacherLink = lessonRow.select("td.align-middle div:not(.mb-2) a");
                    if (!teacherLink.isEmpty()) {
                        teacherName = teacherLink.first().text();
                    } else {
                        Elements teacherDiv = lessonRow.select("td.align-middle");
                        if (!teacherDiv.isEmpty()) {
                            teacherName = teacherDiv.first().ownText();
                            if (teacherName == null || teacherName.trim().isEmpty()) {
                                Elements innerDiv = teacherDiv.select("div:not(.mb-2)");
                                if (!innerDiv.isEmpty()) {
                                    teacherName = innerDiv.first().text();
                                }
                            }
                        }
                    }
                    lesson.setTeacher(teacherName);

                    daySchedule.addLesson(lesson);
                }

                schedule.add(daySchedule);
            }

        } catch (IOException e) {
            Log.e("Расписание","Ошибка при подключении к странице расписания: " + e.getMessage());
            return null;
        }
        return schedule;
    }

    private static boolean isOddWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());
        return weekNumber % 2 != 0;
    }
}
