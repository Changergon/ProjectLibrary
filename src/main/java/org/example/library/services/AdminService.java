package org.example.library.services;

import org.example.library.models.DTO.UserRoleUpdateDTO;
import org.example.library.models.Faculty;
import org.example.library.models.LibraryUser;
import org.example.library.models.Role;
import org.example.library.repositories.FacultyRepository;
import org.example.library.repositories.LibraryUserRepository;
import org.example.library.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    private LibraryUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FacultyRepository facultyRepository; // Добавьте это


    public LibraryUser createUser(LibraryUser user) {
        // Роль будет установлена в UserService
        logger.debug("Method createUser with parameters: user {} ", user);
        return userRepository.save(user);
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        LibraryUser user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);

        if (user == null) {
            System.out.println("Пользователь с ID " + userId + " не найден.");
            return; // Или выбросьте исключение
        }

        if (role == null) {
            System.out.println("Роль с ID " + roleId + " не найдена.");
            return; // Или выбросьте исключение
        }

        // Добавление роли в список ролей пользователя
        user.getRoles().add(role);
        userRepository.save(user);
        System.out.println("Роль " + role.getRoleName() + " была назначена пользователю " + user.getUsername());
    }

    public List<LibraryUser> getAllUsers() {
        List<LibraryUser> users = userRepository.findAll();
        System.out.println("Количество пользователей: " + users.size()); // Вывод количества пользователей
        return users;
    }


    public void updateUserRoles(List<UserRoleUpdateDTO> updates) {
        for (UserRoleUpdateDTO update : updates) {
            LibraryUser user = userRepository.findById(update.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Получение ролей по именам
            List<Role> roles = roleRepository.findAllByRoleNameIn(update.getRoles());
            user.setRoles(new HashSet<>(roles)); // Используйте Set для уникальности

            // Обновление факультетов
            List<Faculty> faculties = facultyRepository.findAllById(update.getFacultyIds());
            user.setFaculties(new HashSet<>(faculties)); // Убедитесь, что вы используете Set для уникальности

            userRepository.save(user); // Сохранение обновленного пользователя
        }
    }


    public void assignFacultyToUser(Long userId, Long facultyId) {
        LibraryUser user = userRepository.findById(userId).orElse(null);
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);

        if (user == null) {
            System.out.println("Пользователь с ID " + userId + " не найден.");
            return; // Или выбросьте исключение
        }

        if (faculty == null) {
            System.out.println("Факультет с ID " + facultyId + " не найден.");
            return; // Или выбросьте исключение
        }

        // Добавление факультета в список факультетов пользователя
        user.getFaculties().add(faculty);
        userRepository.save(user);
        System.out.println("Факультет " + faculty.getType() + " был назначен пользователю " + user.getUsername());
    }

    public void removeFacultyFromUser(Long userId, Long facultyId) {
        LibraryUser user = userRepository.findById(userId).orElse(null);
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);

        if (user == null) {
            System.out.println("Пользователь с ID " + userId + " не найден.");
            return; // Или выбросьте исключение
        }

        if (faculty == null) {
            System.out.println("Факультет с ID " + facultyId + " не найден.");
            return; // Или выбросьте исключение
        }

        // Удаление факультета из списка факультетов пользователя
        user.getFaculties().remove(faculty);
        userRepository.save(user);
        System.out.println("Факультет " + faculty.getType() + " был удален у пользователя " + user.getUsername());
    }


}
