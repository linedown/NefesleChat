package ru.linedown.nefeslechat;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ru.linedown.nefeslechat", appContext.getPackageName());

    }

    @Test
    public void outputJson() {
        final String path = "data/data.json";
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        try(InputStream inputStream = context.getAssets().open(path)){
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Accessory>>(){}.getType();

            List<Accessory> accessories = gson.fromJson(reader, listType);

            for(Accessory accessory : accessories) System.out.println(accessory);
        } catch(IOException ie){
            System.out.println(ie.getMessage());
        }
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

