package ru.linedown.nefeslechat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LastMessageDTO {

    private MessageTypeEnum type;

    private String text;

    private boolean seen;

    @JsonProperty("created_at")
    private Date createdAt;
}
