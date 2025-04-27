package ru.linedown.nefeslechat.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.JavaNetCookieJar;
import ru.linedown.nefeslechat.databinding.ActivityRegisterBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private final String domain = "http://linedown.ru:3254/api";
    final String LOGIN_KEY = "login_key";
    final String PASSWORD_KEY = "password_key";
    Disposable disposable;

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
            String loginText = mailField.getText().toString();
            String lastNameText = loginField.getText().toString();
            String tokenText = tokenField.getText().toString();
            String passwordText = passwordField.getText().toString();
            String password2Text = passwordField2.getText().toString();
            try {
                disposable =
                        verificationRegistration(loginText, lastNameText, tokenText, passwordText, password2Text,
                                new MyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("OK")){
                                            Toast.makeText(RegisterActivity.this, "Вы зарегистрировали аккаунт " + loginText, Toast.LENGTH_SHORT).show();
                                            SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                                            sharedPreferences.edit().putString(LOGIN_KEY, loginText).apply();
                                            sharedPreferences.edit().putString(PASSWORD_KEY, passwordText).apply();
                                            transitionToMessenger();
                                        } else Toast.makeText(RegisterActivity.this, "Ошибка: " + result, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Log.d("ObserveError", "Текст ошибки: " + errorMessage);
                                    }
                                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public Disposable verificationRegistration(String login, String lastName, String token, String password, String password2, MyCallback callback) throws IOException {

        Observable<String> observable = Observable.fromCallable(() -> {
            try{
                if(login.isBlank() || lastName.isBlank() || token.isBlank() || password.isBlank() || password2.isBlank())
                    return "Не все поля заполнены!";
                if(!password.equals(password2)) return "Пароли не совпадают!";

                // Отправлять HttpRequest на сервер для проверки корректности login, lastName и использовании токена
                JsonObject json = new JsonObject();
                json.addProperty("reg_token", token);
                json.addProperty("last_name", lastName);
                json.addProperty("password", password);
                json.addProperty("email", login);
                OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ALL))).build();
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestbody = RequestBody.create(String.valueOf(json), JSON);

                Request request = new Request.Builder().url(domain + "/auth/register")
                        .post(requestbody).build();

                Response response = okHttpClient.newCall(request).execute();
                String bodyResponse = response.body().string();

                if(response.isSuccessful()) return "OK";
                else return bodyResponse;
            } catch (Exception e){
                Log.d("AsyncException", "Текст исключения: " + e.getMessage());
            }
            return "Пусто";
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onSuccess,
                        error -> callback.onError(error.getMessage())
                );
    }

    public void transitionToMessenger(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
