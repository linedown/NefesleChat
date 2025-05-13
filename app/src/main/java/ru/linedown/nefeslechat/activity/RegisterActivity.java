package ru.linedown.nefeslechat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.ActivityRegisterBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.entity.RegistrationForm;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    final String JWT_TOKEN = "jwt_token";
    Disposable disposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ImageButton registerButton = binding.registerButton;
        final TextView loginTransitionButton = binding.onSignInButton;

        final EditText mailField = binding.mailRegister;
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
                                new MyCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if(result.equals("OK")){
                                            Toast.makeText(RegisterActivity.this, "Вы зарегистрировали аккаунт " + loginText, Toast.LENGTH_SHORT).show();
                                            SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                                            sharedPreferences.edit().putString(JWT_TOKEN, OkHttpUtil.getJWTToken()).apply();
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

                RegistrationForm rf = new RegistrationForm();
                rf.setEmail(login);
                rf.setLast_name(lastName);
                rf.setReg_token(token);
                rf.setPassword(password);

                Response response = OkHttpUtil.processAuthentification(rf);
                ResponseBody responseBody = response.body();
                String responseStr = responseBody.string();

                response.close();
                responseBody.close();

                if(response.isSuccessful()) return "OK";
                else return responseStr;
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
