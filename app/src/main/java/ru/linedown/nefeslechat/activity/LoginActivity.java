package ru.linedown.nefeslechat.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.ActivityLoginBinding;
import ru.linedown.nefeslechat.entity.AuthorizationForm;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    final String JWT_TOKEN = "jwt_token";
    private ActivityLoginBinding binding;
    String savedToken;
    EditText loginText;
    EditText passwordText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginText = binding.usernameLogin;
        passwordText = binding.passwordLogin;

        loadSharedToken();
        if(!savedToken.isEmpty()) {
            OkHttpUtil.setJWTToken(savedToken);
            transitionToMessenger();
        }

        final ImageButton loginButton = binding.loginButton;
        final TextView registerTransitionButton = binding.signUpTransitionButton;

        loginButton.setOnClickListener(v -> verificationAuthorization(new MyCallback<String>() {
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
    }

    private void verificationAuthorization(MyCallback callback) {
        String login = loginText.getText().toString();
        String password = passwordText.getText().toString();

        Observable<String> observable = Observable.fromCallable(() -> {
            try{
                if(login.isBlank() || password.isBlank()) return "Не все поля заполнены!";

                AuthorizationForm af = new AuthorizationForm();
                af.setEmail(login);
                af.setPassword(password);

                Response response = OkHttpUtil.processAuthentification(af);
                ResponseBody responseBody = response.body();
                String responseStr = responseBody.string();
                response.close();
                responseBody.close();
                if(!response.isSuccessful()) return "Ошибка: " + responseStr;
                return "OK";
            } catch(Exception e){
                Log.d("AsyncException", "Текст исключения: " + e.getMessage());
            }
            return "Пусто";
        });

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onSuccess,
                        error -> callback.onError(error.getMessage())
                );
    }
    private void saveToken(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        Log.d("Токен пользователя", savedToken);
        sharedPreferences.edit().putString(JWT_TOKEN, OkHttpUtil.getJWTToken()).apply();
    }

    private void loadSharedToken(){
        sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        savedToken = sharedPreferences.getString(JWT_TOKEN, "");
    }

    private void transitionToMessenger(){
        saveToken();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}