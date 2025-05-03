package ru.linedown.nefeslechat.ui.chat;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.MessageDTO;
import ru.linedown.nefeslechat.classes.MyStompSessionHandler;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.FragmentChatBinding;
import ru.linedown.nefeslechat.entity.MessageTypeEnum;
import ru.linedown.nefeslechat.entity.WebSocketDTO;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class ChatFragment extends Fragment {
    final String JWT_TOKEN = "jwt_token";
    private FragmentChatBinding binding;
    private Toolbar toolbar;
    private int userId;
    StompSession session;
    Disposable disposable;
    Disposable disposableInner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        toolbar = getActivity().findViewById(R.id.toolbar);
        Bundle arguments = getArguments();
        String toolbarTitle = arguments.getString("TitleToolBar");
        toolbar.setTitle(toolbarTitle);
        userId = Integer.parseInt(arguments.getString("UserId"));

        OkHttpUtil.setMyId(Integer.parseInt(getActivity()
                .getSharedPreferences("LoginInfo", MODE_PRIVATE)
                .getString("id", "0")));

        EditText inputField = binding.messageText;
        ImageView sendTextButton = binding.sendImage;

        Observable<StompSession> observable = Observable.fromCallable(() -> {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
                    + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();
            StompSessionHandler sessionHandler = new MyStompSessionHandler();
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

            headers.add("JWT", getActivity()
                    .getSharedPreferences("LoginInfo", MODE_PRIVATE).getString(JWT_TOKEN, ""));

            CompletableFuture<StompSession> connection = stompClient.connectAsync(url, headers, sessionHandler);

            return connection.get();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        MyCallback<StompSession> myCallback = new MyCallback<>(){

            @Override
            public void onSuccess(StompSession result) {
                session = result;
                Log.d("Подключение к сессии", "Успешно подключено.  Session ID: " + session.getSessionId());
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("Подключение к сессии: исключение", "Текст исключения: " + errorMessage);
            }
        };

        disposable = observable.subscribe(myCallback::onSuccess, error -> myCallback.onError(error.getMessage()));

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
                            session.send(OkHttpUtil.getUserUrl() + userId, result);
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
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (disposableInner != null && !disposableInner.isDisposed()) {
            disposableInner.dispose();
        }
        binding = null;
    }
}