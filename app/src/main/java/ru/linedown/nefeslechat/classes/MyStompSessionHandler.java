package ru.linedown.nefeslechat.classes;

import static android.content.Context.MODE_PRIVATE;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ru.linedown.nefeslechat.entity.WebSocketDTO;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private final PublishSubject<String> messageSubject;
    public MyStompSessionHandler(PublishSubject<String> messageSubject) {
        this.messageSubject = messageSubject;
    }
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        Log.d("Мой айди: ", "" + OkHttpUtil.getMyId());
        session.subscribe(OkHttpUtil.getTopicUrl() + OkHttpUtil.getMyId(), new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Log.d("1!!!!!!!!", "!!!!!!");
                Log.d("2!!!!!!!!", "!!!!!!");
                Object messagePayloadObj = ((WebSocketDTO) payload).getPayload();
                MessagePayload messagePayload = new ObjectMapper().convertValue(messagePayloadObj, MessagePayload.class);
                Log.d("Сообщение: ", messagePayload.getMessage());

                messageSubject.onNext(messagePayload.getMessage());
                Log.d("После onNext ", "работает");
            }
        });
    }
}