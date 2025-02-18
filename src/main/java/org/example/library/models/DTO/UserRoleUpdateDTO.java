package org.example.library.models.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRoleUpdateDTO {
    private Long userId;
    private List<String> roles; // Список ролей
    private List<Long> facultyIds; // Измените на список идентификаторов факультетов
}
