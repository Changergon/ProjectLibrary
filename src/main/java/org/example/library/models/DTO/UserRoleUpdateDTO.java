package org.example.library.models.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRoleUpdateDTO {
    private Long userId;
    private List<Long> roleIds; // Список идентификаторов ролей
    private List<Long> facultyIds; // Список идентификаторов факультетов
}
