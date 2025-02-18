package org.example.library.models.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotBlank(message = "Роль не может быть пустой")
    private String role; // Добавлено поле для роли
}
