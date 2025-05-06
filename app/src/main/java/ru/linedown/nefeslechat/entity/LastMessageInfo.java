package ru.linedown.nefeslechat.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LastMessageInfo {
    String message;
    MessageTypeEnum type;
    Date createOn;
}
