package org.example.library.repositories;

import org.example.library.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName); // Метод для поиска роли по имени

    List<Role> findAllByRoleNameIn(List<String> roleNames); // Метод для поиска ролей по списку имен
}
