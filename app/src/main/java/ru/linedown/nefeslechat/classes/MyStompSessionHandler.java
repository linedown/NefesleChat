package ru.linedown.nefeslechat.classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.linedown.nefeslechat.Activity.LoginActivity;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.WebSocketDTO;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private final PublishSubject<String> messageSubject;
    public static StompFrameHandler sfh;
    public MyStompSessionHandler(PublishSubject<String> messageSubject) {
        this.messageSubject = messageSubject;
        sfh = new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Log.d("1!!!!!!!!", "!!!!!!");
                Log.d("2!!!!!!!!", "!!!!!!");
                Object messagePayloadObj = ((WebSocketDTO) payload).getPayload();
                MessageAllInfoDTO message = new ObjectMapper().convertValue(messagePayloadObj, MessageAllInfoDTO.class);
                Log.d("Сообщение: ", message.getMessage());

                messageSubject.onNext(new Gson().toJson(message));

                Log.d("После onNext ", "работает");
            }
        };
    }
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        Log.d("Мой айди: ", "" + OkHttpUtil.getMyId());
        session.subscribe(OkHttpUtil.getTopicUrl() + OkHttpUtil.getMyId(), sfh);
        session.subscribe(OkHttpUtil.getGroupSubscribeChatsUrl() + 1, sfh);
    }
}