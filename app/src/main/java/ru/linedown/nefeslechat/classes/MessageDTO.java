package ru.linedown.nefeslechat.classes;

import ru.linedown.nefeslechat.entity.MessageTypeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private MessageTypeEnum type;

    private String message;
}

