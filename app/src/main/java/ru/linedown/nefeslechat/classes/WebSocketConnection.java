package ru.linedown.nefeslechat.classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class WebSocketConnection {
    private static Disposable disposable;
    private static final String JWT_TOKEN = "jwt_token";
    private static StompSession session;
    private static PublishSubject<String> messageSubject = PublishSubject.create();
    private final String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
            + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();

    public static void connection(Context context){
        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
                    + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();
            StompSessionHandler sessionHandler = new MyStompSessionHandler(messageSubject);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

            headers.add("JWT", context
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
    }
}
