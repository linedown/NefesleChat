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
public class UserDetailsDTO {

    private Integer id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String role;

    private Date enabledFrom;

    private Date enabledUntil;

    private String reimbursement;

    private String groupName;

    private String department;

    private String academicTitle;

    private String academicDegree;

    private Boolean isBlocked;

    private String email;
}
