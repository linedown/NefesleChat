package ru.linedown.nefeslechat.layuots;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.activity.LoginActivity;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.interfaces.MyCallback;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.utils.WebSocketConnection;

public class ConfirmExitDialogFragment extends DialogFragment {
    final String JWT_TOKEN = "jwt_token";
    SharedPreferences sharedPreferences;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы точно хотите выйти из аккаунта?");
        builder.setPositiveButton("Выйти", (dialog, which) -> logoutAction(getActivity(), dialog));
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.setCancelable(true);

        return builder.create();
    }

    private void logoutAction(Context context, DialogInterface dialog){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
        sharedPreferences = context.getSharedPreferences("LoginInfo", MODE_PRIVATE);
        Log.d("Токен перед выходом: ", sharedPreferences.getString(JWT_TOKEN, ""));
        sharedPreferences.edit().clear().apply();
        Observable<String> observable = Observable.fromCallable(() -> {
            OkHttpUtil.clearCookies();
            return "Успешно";
        });

        MyCallback<String> mc = new MyCallback<>() {
            @Override
            public void onSuccess(String result) {
                Log.d("Результат: ", result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("Ошибка: ", errorMessage);
            }
        };

        Disposable disposable = observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        mc::onSuccess,
                        error -> mc.onError(error.getMessage()
                ));

        dialog.cancel();
        disposable.dispose();
        WebSocketConnection.disconnect();
        startActivity(intent);
    }
}
