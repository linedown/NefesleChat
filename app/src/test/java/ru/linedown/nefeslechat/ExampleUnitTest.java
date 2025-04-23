package ru.linedown.nefeslechat;

import org.junit.Test;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import androidx.test.platform.app.InstrumentationRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void parseJson(){
        String jsonStr = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"Мужская футболка linedown\",\n" +
                "        \"category_main\": \"man\",\n" +
                "        \"category_inner\": \"футболки\",\n" +
                "        \"color\": \"Чёрный\",\n" +
                "        \"price\": 990,\n" +
                "        \"src\": \"../includes/images/blackTshirtLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 2,\n" +
                "        \"name\": \"Мужские шорты linedown\",\n" +
                "        \"category_main\": \"man\",\n" +
                "        \"category_inner\": \"шорты\",\n" +
                "        \"color\": \"Зелёный\",\n" +
                "        \"price\": 890,\n" +
                "        \"src\": \"../includes/images/greenShortsLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 3,\n" +
                "        \"name\": \"Мужские шорты linedown\",\n" +
                "        \"category_main\": \"man\",\n" +
                "        \"category_inner\": \"шорты\",\n" +
                "        \"color\": \"Чёрный\",\n" +
                "        \"price\": 890,\n" +
                "        \"src\": \"../includes/images/blackShortsLinedown.jpeg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 4,\n" +
                "        \"name\": \"Женские ботинки linedown\",\n" +
                "        \"category_main\": \"woman\",\n" +
                "        \"category_inner\": \"ботинки\",\n" +
                "        \"color\": \"Серый\",\n" +
                "        \"price\": 2590,\n" +
                "        \"src\": \"../includes/images/geryBootsLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 5,\n" +
                "        \"name\": \"Женская толстовка linedown\",\n" +
                "        \"category_main\": \"woman\",\n" +
                "        \"category_inner\": \"толстовки\",\n" +
                "        \"color\": \"Серый\",\n" +
                "        \"price\": 4990,\n" +
                "        \"src\": \"../includes/images/greyHoodiesLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 6,\n" +
                "        \"name\": \"Женские ботинки linedown\",\n" +
                "        \"category_main\": \"woman\",\n" +
                "        \"category_inner\": \"ботинки\",\n" +
                "        \"color\": \"Синий\",\n" +
                "        \"price\": 2590,\n" +
                "        \"src\": \"../includes/images/blueBootsLinedown.webp\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 7,\n" +
                "        \"name\": \"USB-накопитель linedown 64 гб\",\n" +
                "        \"category_main\": \"accessories\",\n" +
                "        \"category_inner\": \"флешки\",\n" +
                "        \"color\": \"Синий\",\n" +
                "        \"price\": 499,\n" +
                "        \"src\": \"../includes/images/blueUSBLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 8,\n" +
                "        \"name\": \"USB-накопитель linedown 32 гб\",\n" +
                "        \"category_main\": \"accessories\",\n" +
                "        \"category_inner\": \"флешки\",\n" +
                "        \"color\": \"Чёрный\",\n" +
                "        \"price\": 499,\n" +
                "        \"src\": \"../includes/images/blackUSBLinedown.jpeg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 9,\n" +
                "        \"name\": \"Кружка linedown\",\n" +
                "        \"category_main\": \"accessories\",\n" +
                "        \"category_inner\": \"кружки\",\n" +
                "        \"color\": \"Зелёный\",\n" +
                "        \"price\": 699,\n" +
                "        \"src\": \"../includes/images/greenCupLinedown.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 10,\n" +
                "        \"name\": \"Кружка linedown\",\n" +
                "        \"category_main\": \"accessories\",\n" +
                "        \"category_inner\": \"кружки\",\n" +
                "        \"color\": \"Белый\",\n" +
                "        \"price\": 699,\n" +
                "        \"src\": \"../includes/images/whiteCap.jpg\"\n" +
                "    }\n" +
                "]";

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Accessory>>(){}.getType();

        List<Accessory> accessories = gson.fromJson(jsonStr, listType);

        for(Accessory accessory : accessories) System.out.println(accessory);
    }

    @Test
    public void sortBooks(){

    List<Book> books = List.of(
            new Book("Шантарам", "Грегори Дэвид Робертс", 1, 780.0, "Москва", "₽"),
            new Book("Три товарища", "Эрих Мария Ремарк", 2, 480.0, "Москва", "₽"),
            new Book("Цветы для Элджернона", "Даниел Киз", 3, 380.0, "Москва", "₽"),
            new Book(" Атлант расправил плечи", "Айн Рэнд", 4, 880.0, "Ставрополь", "₽"),
            new Book(" Атлант расправил плечи", "Айн Рэнд", 4, 580.0, "Сочи", "₽")
    );

    Observable.fromIterable(books).filter(book -> book.location.equals("Москва")).filter(book -> book.price > 400.0)
            .distinct(book -> book.title).map(book -> "Автор книги: " + book .author + " название книги: " + book.title)
            .subscribe(System.out::println);
    }
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