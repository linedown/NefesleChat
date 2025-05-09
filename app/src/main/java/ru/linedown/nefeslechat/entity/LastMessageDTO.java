package ru.linedown.nefeslechat.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LastMessageDTO {

    private MessageTypeEnum type;

    private String text;

    private boolean seen;

    @SerializedName("created_at")
    private Date createdAt;
}
