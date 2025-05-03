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
import ru.linedown.nefeslechat.entity.WebSocketDTO;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(OkHttpUtil.getTopicUrl() + OkHttpUtil.getMyId(), new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Object messagePayloadObj = ((WebSocketDTO) payload).getPayload();
                MessagePayload messagePayload = new ObjectMapper().convertValue(messagePayloadObj, MessagePayload.class);
                // Сделать логику вывода сообщения (внутри фонового потока)
                //System.out.printf("%s\n", messagePayload.getMessage());
//                Observable.fromCallable(() -> {
//
//                })

                Log.d("Сообщение: ", messagePayload.getMessage());

                //OkHttpUtil.setTextMessage(messagePayload.getMessage());
            }
        });
    }
}