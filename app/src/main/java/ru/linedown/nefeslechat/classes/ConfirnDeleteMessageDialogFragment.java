package ru.linedown.nefeslechat.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AllArgsConstructor;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.WebSocketDTO;


@AllArgsConstructor
public class ConfirnDeleteMessageDialogFragment extends DialogFragment {
    private LinearLayout chatLayout;
    private MessageLayout messageLayout;
    private int chatId;
    private int userId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы точно хотите удалить сообщение?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            deleteAction(dialog);
            chatLayout.removeView(messageLayout);
        });
        builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
        builder.setCancelable(true);

        return builder.create();
    }

    private void deleteAction(DialogInterface dialog){
        int messageId = messageLayout.getId();
        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketDTO webSocketDTO = new WebSocketDTO("deleteMessage", messageId);
            if (WebSocketConnection.isConnected()){
                if(messageLayout.getMessageLayoutAttributes().getChatType().equals("Single"))
                    WebSocketConnection.getSession().send(OkHttpUtil.getUserUrl() + userId, webSocketDTO);
                else WebSocketConnection.getSession().send(OkHttpUtil.getChatUrl() + chatId, webSocketDTO);

                return "Успешно удалено";
            }
            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не удалено.");

            return "Не получилось удалить";
        });

        Disposable deleteActionDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                        Log.d("ConfirmDialog", "Результат: " + result);
                        dialog.cancel();
                    }, error -> {
                    Log.e("Удаление: исключение", "Текст исключения: " + error.getMessage(), error);
                    dialog.cancel();
                });

        //deleteActionDisposable.dispose();
    }
}
