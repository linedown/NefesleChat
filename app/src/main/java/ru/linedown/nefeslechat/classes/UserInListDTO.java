package ru.linedown.nefeslechat.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInListDTO {
    private int id;

    private String name;

    private String role;

    private String department;

    private String group;
}
