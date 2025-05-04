package ru.linedown.nefeslechat.ui.chat;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
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
    Disposable messageDisposable;
    private final PublishSubject<String> messageSubject = PublishSubject.create(); // рассмотреть вариант с ReplaySubject

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        toolbar = getActivity().findViewById(R.id.toolbar);
        Bundle arguments = getArguments();
        String toolbarTitle = arguments.getString("TitleToolBar");
        toolbar.setTitle(toolbarTitle);
        userId = Integer.parseInt(arguments.getString("UserId"));
        Log.d("Id собеседника: ", "" + userId);

        OkHttpUtil.setMyId(Integer.parseInt(getActivity()
                .getSharedPreferences("LoginInfo", MODE_PRIVATE)
                .getString("id", "0")));

        EditText inputField = binding.messageText;
        ImageView sendTextButton = binding.sendImage;
        TextView messageView = binding.messageView;
        LinearLayout chatFormLayout = binding.chatFormLayout;

        messageDisposable = messageSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(
                message -> {
                    if(!message.isEmpty()) {
                        messageView.setText("");
                        messageView.setVisibility(GONE);
                    };
                    TextView textView = new TextView(getActivity());
                    textView.setText(message);
                    chatFormLayout.addView(textView);
                });

        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
                    + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();
            StompSessionHandler sessionHandler = new MyStompSessionHandler(messageSubject);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

            headers.add("JWT", getActivity()
                    .getSharedPreferences("LoginInfo", MODE_PRIVATE).getString(JWT_TOKEN, ""));

            CompletableFuture<StompSession> connection = stompClient.connectAsync(url, headers, sessionHandler);

            session = connection.join();
            return "OK";
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        MyCallback<String> myCallback = new MyCallback<>(){

            @Override
            public void onSuccess(String result) {
                Log.d("Подключение к сессии", "Успешно подключено.  Session ID: " + session.getSessionId());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Подключение к сессии: исключение", "Текст исключения: " + errorMessage);
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
                            if (session != null && session.isConnected()){
                                session.send(OkHttpUtil.getUserUrl() + userId, result);
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

        if (session != null) {
            if (session.isConnected()) {
                Log.d("ChatFragment", "WebSocket соединение активно. Отключаемся...");
                Completable.fromAction(() -> {
                            session.disconnect();
                            Log.d("ChatFragment", "WebSocket соединение отключено.");
                        })
                        .subscribeOn(Schedulers.io()) // Выполняем disconnect в IO потоке
                        .subscribe(() -> {}, error -> Log.e("ChatFragment", "Ошибка отключения WebSocket", error));
            } else Log.d("ChatFragment", "WebSocket соединение уже не активно.");
        } else Log.d("ChatFragment", "WebSocket session равна null.");


        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d("ChatFragment", "Disposable (observable) отписан");
        }
        if (disposableInner != null && !disposableInner.isDisposed()) {
            disposableInner.dispose();
            Log.d("ChatFragment", "Disposable (observableInner) отписан");
        }
        if (messageDisposable != null && !messageDisposable.isDisposed()) {
            messageDisposable.dispose();
            Log.d("ChatFragment", "Disposable (messageDisposable) отписан");
        }
        messageSubject.onComplete();
        Log.d("ChatFragment", "Subject завершён");
        binding = null;
        Log.d("ChatFragment", "binding обнулен");
    }
}