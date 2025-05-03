package ru.linedown.nefeslechat.classes;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.linedown.nefeslechat.entity.MessageTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LastMessageInfo {
    String message;
    MessageTypeEnum type;
    Date createOn;
}
