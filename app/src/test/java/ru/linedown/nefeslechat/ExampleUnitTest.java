package ru.linedown.nefeslechat;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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