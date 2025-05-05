package ru.linedown.nefeslechat.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageLayoutAttributes {
    int id;
    Date sendDate;
    String message;
    String filename;
}
