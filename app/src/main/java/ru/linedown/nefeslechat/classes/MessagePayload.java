package ru.linedown.nefeslechat.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.linedown.nefeslechat.entity.MessageTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessagePayload {

    private int id;

    private String message;

    @JsonProperty("chat_id")
    private int chatId;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("chat_name")
    private String chatName;

    private MessageTypeEnum type;

    private String filename;

    @JsonProperty("created_at")
    private Date createdAt;

    private boolean seen;
}
