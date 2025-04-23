package ru.linedown.nefeslechat.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.linedown.nefeslechat.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {


    private ActivityRegisterBinding binding;
    private final String domain = "http://linedown.ru:3254/api";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button registerButton = binding.registerButton;
        final Button loginTransitionButton = binding.onSignInButton;

        final EditText mailField = binding.maleRegister;
        final EditText loginField = binding.lastName;
        final EditText tokenField = binding.tokenRegister;
        final EditText passwordField = binding.passwordRegister;
        final EditText passwordField2 = binding.passwordRegister2;

        registerButton.setOnClickListener(v -> {
            try {
                verificationRegistration(mailField.getText().toString(), loginField.getText().toString(),
                        tokenField.getText().toString(), passwordField.getText().toString(), passwordField2.getText().toString());
            } catch (IOException e) {
                Log.d("VerificationException", "Исключение при исполнение запроса " + e.getMessage());
            }
        });

        loginTransitionButton.setOnClickListener(v -> {
            //Toast.makeText(RegisterActivity.this, "Проверка обработчика событий", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

    public void verificationRegistration(String login, String lastName, String token, String password, String password2) throws IOException {
        if(login.isBlank() || lastName.isBlank() || token.isBlank() || password.isBlank() || password2.isBlank()){
            Toast.makeText(RegisterActivity.this, "Не все поля заполнены!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(password2)){
            Toast.makeText(RegisterActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Отправлять HttpRequest на сервер для проверки корректности login и lastName
        JsonObject json = new JsonObject();
        json.addProperty("reg_token", token);
        json.addProperty("last_name", lastName);
        json.addProperty("password", password);
        json.addProperty("email", login);
        OkHttpClient okHttpClient = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestbody = RequestBody.create(String.valueOf(json), JSON);

        Request request = new Request.Builder().url(domain + "/auth/register")
                .post(requestbody).build();
        // Следующие две строки - заглушки. ОБЯЗАТЕЛЬНО ДОДЕЛАТЬ!
        Response response = okHttpClient.newCall(request).execute();
        System.out.println("Напишу чё-нибудь" + response.body().string());

        Toast.makeText(RegisterActivity.this, "Вы зарегистрировали аккаунт " + login, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }
}
