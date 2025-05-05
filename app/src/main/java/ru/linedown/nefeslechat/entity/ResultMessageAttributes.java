package ru.linedown.nefeslechat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResultMessageAttributes {
    private int id;
    private String fio;
    private String info;
    private int backgroundResource;
}
