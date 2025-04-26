package ru.linedown.nefeslechat;

import org.json.JSONObject;
import org.junit.Test;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.neovisionaries.ws.client.*;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testingWedSocket() throws InterruptedException {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new StringMessageConverter());

        String url = "ws://linedown.ru:8000/portfolio";

        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        stompClient.connectAsync(url, sessionHandler);
        stompClient.start();
        Thread.sleep(60_000);
        stompClient.stop();
    }

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
}

class MyStompSessionHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/greeting", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload);
            }

        });
    }
}
class RegistrationForm {
    @SerializedName("reg_token")
    private String reg_token;

    @SerializedName("last_name")
    private String last_name;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;
}
class Accessory{
    @SerializedName("id")
    final int id;
    @SerializedName("name")
    final String name;
    @SerializedName("category_main")
    final String categoryMain;
    @SerializedName("category_inner")
    final String categoryInner;
    @SerializedName("color")
    final String color;
    @SerializedName("price")
    final int price;
    @SerializedName("src")
    final String src;

    public Accessory(int id, String name, String categoryMain, String categoryInner, String color, int price, String src) {
        this.id = id;
        this.name = name;
        this.categoryMain = categoryMain;
        this.categoryInner = categoryInner;
        this.color = color;
        this.price = price;
        this.src = src;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryMain() {
        return categoryMain;
    }

    public String getCategoryInner() {
        return categoryInner;
    }

    public String getColor() {
        return color;
    }

    public int getPrice() {
        return price;
    }

    public String getSrc() {
        return src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Accessory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryMain='" + categoryMain + '\'' +
                ", categoryInner='" + categoryInner + '\'' +
                ", color='" + color + '\'' +
                ", price=" + price +
                ", src='" + src + '\'' +
                '}';
    }
}

class Book{
    final String title;
    final String author;
    final long id;
    final double price;
    final String location;
    final String currency;


    public Book(String title, String author, long id, double price, String location, String currency) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.price = price;
        this.location = location;
        this.currency = currency;
    }
}