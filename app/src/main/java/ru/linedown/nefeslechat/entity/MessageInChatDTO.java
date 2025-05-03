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
class MessageInChatDTO{
    int id; // лучше long
    MessageTypeEnum type; // текст или файл
    Date dateOnCreate;
    String text; // если текстовое
    String fileName; // если файловое (имя + расширение)
    int idSender; // отправитель
    int idRecipient; // получатель
    int idChat; // айди чата в каком находится соо
    boolean view; // просмотрено или нет
    String firstLastNameSender;
}
