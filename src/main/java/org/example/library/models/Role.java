package org.example.library.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", unique = true) // Убедитесь, что имя роли уникально
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude // Исключаем взаимные ссылки
    private List<LibraryUser > libraryUsers; // Список пользователей с этой ролью

    // Конструктор для создания роли с именем
    public Role(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
