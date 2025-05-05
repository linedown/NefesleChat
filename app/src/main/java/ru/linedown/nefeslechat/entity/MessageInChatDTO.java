package ru.linedown.nefeslechat.entity;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageInChatDTO {

    private int id;

    @JsonProperty("sender_id")
    private int senderId;

    @JsonProperty("chat_id")
    private int chatId;

    private MessageTypeEnum type;

    @JsonProperty("created_at")
    private Date createdAt;

    private String message;

    private String filename;

    @JsonProperty("sender_name")
    private String senderName;

    private boolean seen;
}
