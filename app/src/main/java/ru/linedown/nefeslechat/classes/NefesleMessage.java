package ru.linedown.nefeslechat.classes;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NefesleMessage {
    String message;
    String tyoe;
    Date createOn;
}
