package ru.linedown.nefeslechat.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatInnerInfoDTO{
    String title;
    int countOfUsers;
    List<UserInListDTO> users;
}