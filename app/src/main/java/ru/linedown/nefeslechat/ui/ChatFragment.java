package ru.linedown.nefeslechat.ui;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.WebSocketConnection;
import ru.linedown.nefeslechat.entity.MessageDTO;
import ru.linedown.nefeslechat.classes.MessageLayout;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.FragmentChatBinding;
import ru.linedown.nefeslechat.entity.MessageInChatDTO;
import ru.linedown.nefeslechat.entity.MessageLayoutAttributes;
import ru.linedown.nefeslechat.entity.MessageTypeEnum;
import ru.linedown.nefeslechat.entity.WebSocketDTO;

public class ChatFragment extends Fragment {
    final String JWT_TOKEN = "jwt_token";
    private FragmentChatBinding binding;
    private int userId;
    Disposable disposableInner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        Bundle arguments = getArguments();
        String toolbarTitle = arguments.getString("TitleToolBar");
        toolbar.setTitle(toolbarTitle);
        userId = Integer.parseInt(arguments.getString("UserId"));
        Log.d("Id собеседника: ", "" + userId);

        OkHttpUtil.setMyId(Integer.parseInt(getActivity()
                .getSharedPreferences("LoginInfo", MODE_PRIVATE)
                .getString("id", "0")));

        EditText inputField = binding.messageText;
        ImageView sendTextButton = binding.sendTextButton;
        ImageView sendFileButton = binding.sendFileButton;
        TextView messageView = binding.messageView;
        LinearLayout chatFormLayout = binding.chatFormLayout;

        WebSocketConnection.subscribeOnMessageEvent(message -> {
            int typeSender;
            if(!message.isEmpty()) {
                messageView.setText("");
                messageView.setVisibility(GONE);
            }
            MessageInChatDTO messageInChatDTO = new Gson().fromJson(message, MessageInChatDTO.class);
            MessageLayoutAttributes mla = new MessageLayoutAttributes(
                    messageInChatDTO.getId(), messageInChatDTO.getCreatedAt(), messageInChatDTO.getMessage(),
                    messageInChatDTO.getFilename());

            if(messageInChatDTO.getSenderId() == OkHttpUtil.getMyId()) typeSender = MessageLayout.ME;
            else typeSender = MessageLayout.COMPANION;
            MessageLayout messageLayout = new MessageLayout(getActivity(), typeSender, mla);
            chatFormLayout.addView(messageLayout);
        });

        sendTextButton.setOnClickListener(view -> {
            Observable<WebSocketDTO> observableInner = Observable.fromCallable(() -> {
                String text = inputField.getText().toString();
                if(text.isBlank()) return null;
                MessageDTO messageDTO = new MessageDTO(MessageTypeEnum.TEXT, text);
                return new WebSocketDTO("sendMessage", messageDTO);
            });

            disposableInner = observableInner.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .subscribe(result -> {
                        if(result == null) return;
                        try {
                            if (WebSocketConnection.isConnected()){
                                WebSocketConnection.getSession().send(OkHttpUtil.getUserUrl() + userId, result);
                                inputField.setText("");
                            }
                            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не отправлено.");
                        } catch (Exception e) {
                            Log.e("Отправка сообщения", "Ошибка отправки: " + e.getMessage(), e);
                        }
                    }, error -> Log.e("Отправка: исключение", "Текст исключения: " + error.getMessage(), error));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("ChatFragment", "onDestroyView() вызван");

        if (disposableInner != null && !disposableInner.isDisposed()) {
            disposableInner.dispose();
            Log.d("ChatFragment", "Disposable (observableInner) отписан");
        }
        WebSocketConnection.unSubscribeOnMessageEvent();
        binding = null;
        Log.d("ChatFragment", "binding обнулен");
    }
}