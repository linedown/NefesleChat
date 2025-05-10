package ru.linedown.nefeslechat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageAllInfoDTO {

    private int id;

    @SerializedName("sender_id")
    @JsonProperty("sender_id")
    private int senderId;

    @SerializedName("chat_id")
    @JsonProperty("chat_id")
    private int chatId;

    private MessageTypeEnum type;

    @SerializedName("created_at")
    @JsonProperty("created_at")
    private Date createdAt;

    private String message;

    private String filename;

    @SerializedName("sender_name")
    @JsonProperty("sender_name")
    private String senderName;

    @SerializedName("chat_name")
    @JsonProperty("chat_name")
    private String chatName;

    private boolean seen;
}
