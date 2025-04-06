package ru.linedown.nefeslechat.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button loginButton = binding.loginButton;
        final Button registerTransitionButton = binding.signUpTransitionButton;
        final EditText loginText = binding.usernameLogin;

        NotificationChannel notificationChannel = new NotificationChannel("LOGIN", "LOGIN CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        loginButton.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Вы вошли в аккаунт " + loginText.getText(), Toast.LENGTH_SHORT).show();

            Notification notification = new NotificationCompat.Builder(this, "LOGIN")
                    .setContentTitle("Вход")
                    .setContentText("Вы вошли в аккаунт под пользователем " + loginText.getText())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            notificationManager.notify(69, notification);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        registerTransitionButton.setOnClickListener(v -> {
            //Toast.makeText(LoginActivity.this, "Проверка обработчика событий", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }
}