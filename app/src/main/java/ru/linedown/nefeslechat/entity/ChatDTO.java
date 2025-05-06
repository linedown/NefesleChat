package ru.linedown.nefeslechat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private int id;

    private String name;

    private ChatTypeEnum type;

    @JsonProperty("last_message")
    private LastMessageDTO lastMessage;

    @JsonProperty("message_from")
    private String messageFrom;

    @JsonProperty("user_type")
    private RoleEnum userType;
}

