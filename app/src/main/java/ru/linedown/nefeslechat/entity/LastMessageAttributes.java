package ru.linedown.nefeslechat.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LastMessageAttributes {
    int chatId;
    String text;
    String nameChat;
    int chatType;
    boolean readChat;
    Date createdOn;
}
