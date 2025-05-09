package ru.linedown.nefeslechat.entity;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.linedown.nefeslechat.enums.ChatTypeEnum;
import ru.linedown.nefeslechat.enums.RoleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatDTO {

    private int id;

    private String name;

    private ChatTypeEnum type;

    @SerializedName("last_message")
    private LastMessageDTO lastMessage;

    @SerializedName("message_from")
    private String messageFrom;

    @SerializedName("user_type")
    private RoleEnum userType;

    @SerializedName("not_read")
    private int notRead;

    @SerializedName("user_id")
    private int userId;
}

