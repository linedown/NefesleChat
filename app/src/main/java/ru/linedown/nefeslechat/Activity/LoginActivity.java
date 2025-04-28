package ru.linedown.nefeslechat.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.net.CookieManager;
import java.net.CookiePolicy;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.ActivityLoginBinding;
import ru.linedown.nefeslechat.entity.AuthorizationForm;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    final String LOGIN_KEY = "login_key";
    final String PASSWORD_KEY = "password_key";
    private final String domain = "http://linedown.ru:3254/api/auth";
    private ActivityLoginBinding binding;
    String savedLogin;
    String savedPassword;
    EditText loginText;
    EditText passwordText;
    OkHttpClient okHttpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar
                (new CookieManager(null, CookiePolicy.ACCEPT_ALL))).build();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginText = binding.usernameLogin;
        passwordText = binding.passwordLogin;

        loadSharedData();
        if(!savedLogin.isEmpty() && !savedPassword.isEmpty()) transitionToMessenger();

        final Button loginButton = binding.loginButton;
        final Button registerTransitionButton = binding.signUpTransitionButton;

        loginButton.setOnClickListener(v -> verificationAuthorization(new MyCallback() {
            @Override
            public void onSuccess(String result) {
                if(result.equals("OK")){
                    NotificationChannel notificationChannel = new NotificationChannel("LOGIN", "LOGIN CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(notificationChannel);

                    Toast.makeText(LoginActivity.this, "Вы вошли в аккаунт " + loginText.getText(), Toast.LENGTH_SHORT).show();
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(LoginActivity.this, "LOGIN")
                            .setContentTitle("Вход")
                            .setContentText("Вы вошли в аккаунт под пользователем " + loginText.getText())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(69, notification.build());
                    transitionToMessenger();
                } else Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onError(String errorMessage) {
                Log.d("ObserveError", "Текст ошибки: " + errorMessage);
            }
        }));

        registerTransitionButton.setOnClickListener(v -> {
            //Toast.makeText(LoginActivity.this, "Проверка обработчика событий", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLogin();
    }

    private Disposable verificationAuthorization(MyCallback callback) {
        String login = loginText.getText().toString();
        String password = passwordText.getText().toString();

        Observable<String> observable = Observable.fromCallable(() -> {
            try{
                if(login.isBlank() || password.isBlank()) return "Не все поля заполнены!";

                AuthorizationForm af = new AuthorizationForm();
                af.setEmail(login);
                af.setPassword(password);

                Response response = OkHttpUtil.processAuthentification(af);

                if(!response.isSuccessful()) return "Ошибка: " + response.body().string();
                return "OK";
            } catch(Exception e){
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
    private void saveLogin(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        sharedPreferences.edit().putString(LOGIN_KEY, loginText.getText().toString()).apply();
        sharedPreferences.edit().putString(PASSWORD_KEY, passwordText.getText().toString()).apply();
    }

    private void loadSharedData(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        savedLogin = sharedPreferences.getString(LOGIN_KEY, "");
        savedPassword = sharedPreferences.getString(PASSWORD_KEY, "");
    }

    private void transitionToMessenger(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}