package org.example.library.models.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserRoleUpdateDTO {
    private Long userId;
    private List<String> roles; // Изменено на List<String> для приема названий ролей
    private List<Long> facultyIds;
}
