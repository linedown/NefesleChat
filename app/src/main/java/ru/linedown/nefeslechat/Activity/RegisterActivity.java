package ru.linedown.nefeslechat.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.linedown.nefeslechat.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button registerButton = binding.registerButton;
        final Button loginTransitionButton = binding.onSignInButton;
        final EditText registerText = binding.usernameRegister;

        registerButton.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "Вы зарегистрировали аккаунт " + registerText.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        loginTransitionButton.setOnClickListener(v -> {
            //Toast.makeText(RegisterActivity.this, "Проверка обработчика событий", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }
}
