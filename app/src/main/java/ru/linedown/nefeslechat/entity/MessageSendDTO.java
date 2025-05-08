package ru.linedown.nefeslechat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageSendDTO {
    private MessageTypeEnum type;
    private String message;
}

