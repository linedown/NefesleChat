package ru.linedown.nefeslechat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.linedown.nefeslechat.enums.ChatTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatDTO {
    private String name;
    private ChatTypeEnum type;
}
