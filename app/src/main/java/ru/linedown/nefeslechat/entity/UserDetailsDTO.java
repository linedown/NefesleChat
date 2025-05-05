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
public class UserDetailsDTO {
    private Integer id;

    private String firstName;

    private String lastName;

    private String patronymic;
    // Роль: Студент или преподаватель
    private String role;
    // Даты присоединения/ухода
    private Date enabledFrom;
    private Date enabledUntil;
    // Форма возмещения средств: бюджет, контракт, целевое - если роль студента
    private String reimbursement;
    // Название группы - есть у роли студента
    private String groupName;
    // Факультет
    private String department;
    // academicTitle и academicDegree - если преподаватель
    private String academicTitle;
    private String academicDegree;
    // Заблокирован ли аккаунт - в случаен отчисления или увольнения
    private boolean isBlocked;

    private String email;
    private String faculty;
    // Статус онлайна
    private boolean isOnline;

    public boolean getIsOnline() {
        return isOnline;
    }
}
