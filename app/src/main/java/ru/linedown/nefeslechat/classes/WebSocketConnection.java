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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class WebSocketConnection {
    private static Disposable disposable;
    private static Disposable messageDisposable;
    private static Disposable getMessageInChatsDisposable;
    private static final String JWT_TOKEN = "jwt_token";
    @Getter
    private static StompSession session;
    private static PublishSubject<String> messageSubject;
    private static final String url = OkHttpUtil.getWebsocketHeader() + OkHttpUtil.getBaseUrlWithoutApi()
            + OkHttpUtil.getAfterBaseUrl() + OkHttpUtil.getMessagingUrl();

    public static void connection(Context context){
        Observable<String> observable = Observable.fromCallable(() -> {
            messageSubject = PublishSubject.create();
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

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

    public static void subscribeonGetMessageEvent(Consumer<? super String> consumer){
        getMessageInChatsDisposable = messageSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        Log.w("Состояние getMessageInChatsDisposable", getMessageInChatsDisposable.toString());
    }

    public static void unSubscribeOnGetMessageEvent(){
        if(getMessageInChatsDisposable != null && !getMessageInChatsDisposable.isDisposed()){
            getMessageInChatsDisposable.dispose();
            Log.d("Мессенджер", "Subscriber getMessage отписан от события");
        }
    }

    public static void subscribeOnSendMessageEvent(Consumer<? super String> consumer){
        messageDisposable = messageSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public static void unSubscribeOnSendMessageEvent(){
        if(messageDisposable != null && !messageDisposable.isDisposed()){
            messageDisposable.dispose();
            Log.d("Мессенджер", "Subscriber отписан от события");
        }
    }

    public static boolean isConnected(){
        return session != null && session.isConnected();
    }

    public static void send(String destination, Object payload){
        session.send(destination, payload);
    }

    public static void disconnect(){
        if (session != null) {
            if (session.isConnected()) {
                Log.d("Мессенджер", "WebSocket соединение активно. Отключаемся...");
                Completable.fromAction(() -> {
                            session.disconnect();
                            Log.d("Мессенджер", "WebSocket соединение отключено.");
                        })
                        .subscribeOn(Schedulers.io()) // Выполняем disconnect в IO потоке
                        .subscribe(() -> {}, error -> Log.e("Мессенджер", "Ошибка отключения WebSocket", error));
            } else Log.d("Мессенджер", "WebSocket соединение уже не активно.");
        } else Log.d("Мессенджер", "WebSocket session равна null.");


        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d("Мессенджер", "Disposable (observable) отписан");
        }
        if (messageDisposable != null && !messageDisposable.isDisposed()) {
            messageDisposable.dispose();
            Log.d("Мессенджер", "Disposable (messageDisposable) отписан");
        }
        messageSubject.onComplete();
        Log.d("Мессенджер", "Subject завершён");
    }
}
