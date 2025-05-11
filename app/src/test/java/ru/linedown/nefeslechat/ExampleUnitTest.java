package ru.linedown.nefeslechat;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import androidx.test.platform.app.InstrumentationRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
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
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import io.reactivex.rxjava3.core.Observable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.linedown.nefeslechat.entity.WebSocketDTO;

//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//
//import org.springframework.messaging.converter.StringMessageConverter;
//import org.springframework.messaging.simp.stomp.StompSessionHandler;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String BASE_URL = "https://rasp.pgups.ru/schedule/group/";
    private static final String SEARCH_URL = "https://rasp.pgups.ru/search?title=ивб-111&by=group";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Test
    public void parser(){
        try {
            List<DaySchedule> schedule = parseSchedulePage();

            if (schedule != null) {
                for (DaySchedule day : schedule) {
                    System.out.println("День недели: " + day.getDayOfWeek() + " (" + day.getDate() + ")");
                    for (Lesson lesson : day.getLessons()) {
                        System.out.println("  Пара: " + lesson.getParaNumber());
                        if (lesson.getStartTime() != null && lesson.getEndTime() != null) {
                            System.out.println("  Время: " + lesson.getStartTime() + " - " + lesson.getEndTime());
                        } else {
                            System.out.println("  Время: Не определено");
                        }
                        System.out.println("  Кабинет: " + lesson.getRoom());
                        System.out.println("  Предмет: " + lesson.getSubject());
                        System.out.println("  Преподаватель: " + lesson.getTeacher());
                        System.out.println("  Тип занятия: " + lesson.getLessonType());
                        System.out.println("---");
                    }
                    System.out.println("====================");
                }
            } else {
                System.out.println("Не удалось получить расписание.");
            }

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public static List<DaySchedule> parseSchedulePage() throws IOException {
        List<DaySchedule> schedule = new ArrayList<>();
        boolean isOddWeek = isOddWeek();

        try {
            Document searchDoc = Jsoup.connect(SEARCH_URL).get();
            Elements groupLinks = searchDoc.select("#kt_content > div.kt-container.kt-grid__item.kt-grid__item--fluid > div:nth-child(2) > div.kt-portlet__body > div > div > div > a");

            if (groupLinks.isEmpty()) {
                System.err.println("Не найдена ссылка на группу.");
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
                java.sql.Date date = java.sql.Date.valueOf(String.valueOf(currentDate)); // Получаем дату дня недели

                String dayOfWeek = table.previousElementSibling() != null ? table.previousElementSibling().text() : table.select("td.d-none.d-md-table-cell h4").text().replace(" ", "").replace("\n", "");

                if (dayOfWeek.isEmpty()) {
                    continue;
                }
                DaySchedule daySchedule = new DaySchedule(dayOfWeek, date); //Передаем дату в DaySchedule

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
                                System.err.println("Ошибка при парсинге времени: " + timeString);
                                lesson.setStartTime(null);
                                lesson.setEndTime(null);
                            }
                        } else {
                            System.err.println("Неправильный формат времени: " + timeString);
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

                    // Extracting teacher's name
                    String teacherName = null;
                    Elements teacherLink = lessonRow.select("td.align-middle div:not(.mb-2) a");
                    if (!teacherLink.isEmpty()) {
                        // Teacher's name is in the <a> tag
                        teacherName = teacherLink.first().text();
                    } else {
                        // Teacher's name is directly in the <td> tag or in a <div> inside it
                        Elements teacherDiv = lessonRow.select("td.align-middle");
                        if (!teacherDiv.isEmpty()) {
                            teacherName = teacherDiv.first().ownText();
                            if (teacherName == null || teacherName.trim().isEmpty()) {
                                // If still empty, try to get the text from a <div> inside the <td>
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
            System.err.println("Ошибка при подключении к странице расписания: " + e.getMessage());
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

    static class DaySchedule {
        private String dayOfWeek;
        private java.sql.Date date;
        private List<Lesson> lessons = new ArrayList<>();

        public DaySchedule(String dayOfWeek, java.sql.Date date) {
            this.dayOfWeek = dayOfWeek;
            this.date = date;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public java.sql.Date getDate() {
            return date;
        }

        public List<Lesson> getLessons() {
            return lessons;
        }

        public void addLesson(Lesson lesson) {
            lessons.add(lesson);
        }
    }

    static class Lesson {
        private String paraNumber;
        private Date startTime;
        private Date endTime;
        private String room;
        private String subject;
        private String teacher;
        private String lessonType;

        public String getParaNumber() {
            return paraNumber;
        }

        public void setParaNumber(String paraNumber) {
            this.paraNumber = paraNumber;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getLessonType() {
            return lessonType;
        }

        public void setLessonType(String lessonType) {
            this.lessonType = lessonType;
        }

        @Override
        public String toString() {
            return "Lesson{" +
                    "paraNumber='" + paraNumber + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", room='" + room + '\'' +
                    ", subject='" + subject + '\'' +
                    ", teacher='" + teacher + '\'' +
                    ", lessonType='" + lessonType + '\'' +
                    '}';
        }
    }
}

//    @Test
//    public void WebSocketTest() throws ExecutionException, InterruptedException, TimeoutException {
//        WebSocketClient client = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        String url = "ws://linedown.ru:3254/api/messenger";
//
//        StompSessionHandler sessionHandler = new MyStompSessionHandler();
//        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
//        headers.add("JWT", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnZ2dAZ2dnLmNvbSIsImV4cCI6MTc0NzU5MjM3NCwiaWF0IjoxNzQ2Mjc4Mzc0fQ.ebb-DAFSztSOP20JhbiDxn8K1yGDF-jc9HnM84VtI_M3R4bP1WA_NeGQsOSoJsylgOFawsktVi0lK1kY7qSvJw");
//        CompletableFuture<StompSession> connection =  stompClient.connectAsync(url, headers, sessionHandler);
//        StompSession session = connection.get();
//
//        Scanner scanner = new Scanner(System.in);
//
//        String text = "Ужас";
//        //if(scanner.nextLine() != null) text = scanner.nextLine();
//
////            if(text.equals("!")) {
////                break;
////            }
//
//        MessageDTO messageDTO = new MessageDTO(MessageTypeEnum.TEXT, text);
//        WebSocketDTO webSocketDTO = new WebSocketDTO("sendMessage", messageDTO);
//
//        session.send("/app/user/1", webSocketDTO);

//        while(true) {
//            String text = "Ужас";
//            //if(scanner.nextLine() != null) text = scanner.nextLine();
//
////            if(text.equals("!")) {
////                break;
////            }
//
//            MessageDTO messageDTO = new MessageDTO(MessageTypeEnum.TEXT, text);
//            WebSocketDTO webSocketDTO = new WebSocketDTO("sendMessage", messageDTO);
//
//            session.send("/app/user/1", webSocketDTO);
//
//            break;
//        }
//        session.disconnect();
//        scanner.close();
//    }
//}
//    @Test
//    public void testingWedSocket() throws InterruptedException {
//        WebSocketClient client = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//
//        stompClient.setMessageConverter(new StringMessageConverter());
//
//        String url = "ws://linedown.ru:8000/portfolio";
//
//        StompSessionHandler sessionHandler = new MyStompSessionHandler();
//        stompClient.connectAsync(url, sessionHandler);
//        stompClient.start();
//        Thread.sleep(60_000);
//        stompClient.stop();
//    }

//    @Test
//    public void testRequest() throws IOException{
//        final String domain = "http://linedown.ru:3254/api";
//
//        // Отправлять HttpRequest на сервер для проверки корректности login и lastName
//        JsonObject json = new JsonObject();
//        json.addProperty("reg_token", "WZwDqrKCe9boueVT");
//        json.addProperty("last_name", "Федоров");
//        json.addProperty("password", "1234");
//        json.addProperty("email", "elfbrus@gmail.com");
//        OkHttpClient okHttpClient = new OkHttpClient();
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody requestbody = RequestBody.create(String.valueOf(json), JSON);
//
//        Request request = new Request.Builder().url(domain + "/auth/register")
//                .post(requestbody).build();
//        Response response = okHttpClient.newCall(request).execute();
//        System.out.println("Напишу чё-нибудь" + response.body().string());
//    }
//
//
//    @Test
//    public void parseJson(){
//        String jsonStr = "[\n" +
//                "    {\n" +
//                "        \"id\": 1,\n" +
//                "        \"name\": \"Мужская футболка linedown\",\n" +
//                "        \"category_main\": \"man\",\n" +
//                "        \"category_inner\": \"футболки\",\n" +
//                "        \"color\": \"Чёрный\",\n" +
//                "        \"price\": 990,\n" +
//                "        \"src\": \"../includes/images/blackTshirtLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 2,\n" +
//                "        \"name\": \"Мужские шорты linedown\",\n" +
//                "        \"category_main\": \"man\",\n" +
//                "        \"category_inner\": \"шорты\",\n" +
//                "        \"color\": \"Зелёный\",\n" +
//                "        \"price\": 890,\n" +
//                "        \"src\": \"../includes/images/greenShortsLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 3,\n" +
//                "        \"name\": \"Мужские шорты linedown\",\n" +
//                "        \"category_main\": \"man\",\n" +
//                "        \"category_inner\": \"шорты\",\n" +
//                "        \"color\": \"Чёрный\",\n" +
//                "        \"price\": 890,\n" +
//                "        \"src\": \"../includes/images/blackShortsLinedown.jpeg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 4,\n" +
//                "        \"name\": \"Женские ботинки linedown\",\n" +
//                "        \"category_main\": \"woman\",\n" +
//                "        \"category_inner\": \"ботинки\",\n" +
//                "        \"color\": \"Серый\",\n" +
//                "        \"price\": 2590,\n" +
//                "        \"src\": \"../includes/images/geryBootsLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 5,\n" +
//                "        \"name\": \"Женская толстовка linedown\",\n" +
//                "        \"category_main\": \"woman\",\n" +
//                "        \"category_inner\": \"толстовки\",\n" +
//                "        \"color\": \"Серый\",\n" +
//                "        \"price\": 4990,\n" +
//                "        \"src\": \"../includes/images/greyHoodiesLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 6,\n" +
//                "        \"name\": \"Женские ботинки linedown\",\n" +
//                "        \"category_main\": \"woman\",\n" +
//                "        \"category_inner\": \"ботинки\",\n" +
//                "        \"color\": \"Синий\",\n" +
//                "        \"price\": 2590,\n" +
//                "        \"src\": \"../includes/images/blueBootsLinedown.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 7,\n" +
//                "        \"name\": \"USB-накопитель linedown 64 гб\",\n" +
//                "        \"category_main\": \"accessories\",\n" +
//                "        \"category_inner\": \"флешки\",\n" +
//                "        \"color\": \"Синий\",\n" +
//                "        \"price\": 499,\n" +
//                "        \"src\": \"../includes/images/blueUSBLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 8,\n" +
//                "        \"name\": \"USB-накопитель linedown 32 гб\",\n" +
//                "        \"category_main\": \"accessories\",\n" +
//                "        \"category_inner\": \"флешки\",\n" +
//                "        \"color\": \"Чёрный\",\n" +
//                "        \"price\": 499,\n" +
//                "        \"src\": \"../includes/images/blackUSBLinedown.jpeg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 9,\n" +
//                "        \"name\": \"Кружка linedown\",\n" +
//                "        \"category_main\": \"accessories\",\n" +
//                "        \"category_inner\": \"кружки\",\n" +
//                "        \"color\": \"Зелёный\",\n" +
//                "        \"price\": 699,\n" +
//                "        \"src\": \"../includes/images/greenCupLinedown.jpg\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"id\": 10,\n" +
//                "        \"name\": \"Кружка linedown\",\n" +
//                "        \"category_main\": \"accessories\",\n" +
//                "        \"category_inner\": \"кружки\",\n" +
//                "        \"color\": \"Белый\",\n" +
//                "        \"price\": 699,\n" +
//                "        \"src\": \"../includes/images/whiteCap.jpg\"\n" +
//                "    }\n" +
//                "]";
//
//        Gson gson = new Gson();
//        Type listType = new TypeToken<List<Accessory>>(){}.getType();
//
//        List<Accessory> accessories = gson.fromJson(jsonStr, listType);
//
//        for(Accessory accessory : accessories) System.out.println(accessory);
//
//        System.out.println(accessories.getFirst().src);
//
//    }
//
//    @Test
//    public void sortBooks(){
//
//    List<Book> books = List.of(
//            new Book("Шантарам", "Грегори Дэвид Робертс", 1, 780.0, "Москва", "₽"),
//            new Book("Три товарища", "Эрих Мария Ремарк", 2, 480.0, "Москва", "₽"),
//            new Book("Цветы для Элджернона", "Даниел Киз", 3, 380.0, "Москва", "₽"),
//            new Book(" Атлант расправил плечи", "Айн Рэнд", 4, 880.0, "Ставрополь", "₽"),
//            new Book(" Атлант расправил плечи", "Айн Рэнд", 4, 580.0, "Сочи", "₽")
//    );
//
//    Observable.fromIterable(books).filter(book -> book.location.equals("Москва")).filter(book -> book.price > 400.0)
//            .distinct(book -> book.title).map(book -> "Автор книги: " + book .author + " название книги: " + book.title)
//            .subscribe(System.out::println);
//    }
//}

//class MyStompSessionHandler extends StompSessionHandlerAdapter {
//    @Override
//    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//        session.subscribe("/topic/greeting", new StompFrameHandler() {
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return String.class;
//            }
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                System.out.println(payload);
//            }
//
//        });
//    }
//}
//class RegistrationForm {
//    @SerializedName("reg_token")
//    private String reg_token;
//
//    @SerializedName("last_name")
//    private String last_name;
//
//    @SerializedName("password")
//    private String password;
//
//    @SerializedName("email")
//    private String email;
//}
//class Accessory{
//    @SerializedName("id")
//    final int id;
//    @SerializedName("name")
//    final String name;
//    @SerializedName("category_main")
//    final String categoryMain;
//    @SerializedName("category_inner")
//    final String categoryInner;
//    @SerializedName("color")
//    final String color;
//    @SerializedName("price")
//    final int price;
//    @SerializedName("src")
//    final String src;
//
//    public Accessory(int id, String name, String categoryMain, String categoryInner, String color, int price, String src) {
//        this.id = id;
//        this.name = name;
//        this.categoryMain = categoryMain;
//        this.categoryInner = categoryInner;
//        this.color = color;
//        this.price = price;
//        this.src = src;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getCategoryMain() {
//        return categoryMain;
//    }
//
//    public String getCategoryInner() {
//        return categoryInner;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    public String getSrc() {
//        return src;
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return "Accessory{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", categoryMain='" + categoryMain + '\'' +
//                ", categoryInner='" + categoryInner + '\'' +
//                ", color='" + color + '\'' +
//                ", price=" + price +
//                ", src='" + src + '\'' +
//                '}';
//    }
//}
//
//class Book{
//    final String title;
//    final String author;
//    final long id;
//    final double price;
//    final String location;
//    final String currency;
//
//
//    public Book(String title, String author, long id, double price, String location, String currency) {
//        this.title = title;
//        this.author = author;
//        this.id = id;
//        this.price = price;
//        this.location = location;
//        this.currency = currency;
//    }
//}



enum MessageTypeEnum {
    TEXT, FILE
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class MessagePayload {

    private int id;

    private String message;

    @JsonProperty("chat_id")
    private int chatId;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("chat_name")
    private String chatName;

    private MessageTypeEnum type;

    @SerializedName("created_at")
    private Date createdAt;
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class MessageDTO {

    private MessageTypeEnum type;

    private String message;
}

class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/user/1", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Object messagePayloadObj = ((WebSocketDTO) payload).getPayload();
                MessagePayload messagePayload = new ObjectMapper().convertValue(messagePayloadObj, MessagePayload.class);
                System.out.printf("%s\n", messagePayload.getMessage());
            }
        });
    }
}

