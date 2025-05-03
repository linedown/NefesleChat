package ru.linedown.nefeslechat.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WebSocketDTO {
    private String type;

    private Object payload;
}