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

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    final String LOGIN_KEY = "login_key";
    final String TOKEN_KEY = "token_key";
    final String PASSWORD_KEY = "password_key";
    private ActivityLoginBinding binding;
    String savedLogin;
    EditText loginText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginText = binding.usernameLogin;

        loadLogin();
        if(!savedLogin.isEmpty()) transitionToMessenger();

        final Button loginButton = binding.loginButton;
        final Button registerTransitionButton = binding.signUpTransitionButton;

        NotificationChannel notificationChannel = new NotificationChannel("LOGIN", "LOGIN CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        loginButton.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Вы вошли в аккаунт " + loginText.getText(), Toast.LENGTH_SHORT).show();
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "LOGIN")
                    .setContentTitle("Вход")
                    .setContentText("Вы вошли в аккаунт под пользователем " + loginText.getText())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(69, notification.build());

            transitionToMessenger();
        });

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


    private void saveLogin(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        sharedPreferences.edit().putString(LOGIN_KEY, loginText.getText().toString()).apply();
    }

    private void loadLogin(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        savedLogin = sharedPreferences.getString(LOGIN_KEY, "");
    }

    private void transitionToMessenger(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}