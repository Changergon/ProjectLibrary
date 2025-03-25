package org.example.library.controllers;

import org.example.library.models.Book;
import org.example.library.models.DTO.UserDTO;
import org.example.library.models.DTO.UserRoleUpdateDTO;
import org.example.library.models.FacultyType;
import org.example.library.models.LibraryUser ;
import org.example.library.services.AdminService;
import org.example.library.services.BookService; // Импортируйте BookService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.example.library.models.Role; // Добавьте этот импорт


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BookService bookService; // Внедряем BookService

    @PreAuthorize("hasRole('ADMIN')") // Ограничение доступа для администраторов
    @PostMapping("/users")
    public ResponseEntity<LibraryUser > createUser (@RequestBody LibraryUser  user) {
        LibraryUser  createdUser  = adminService.createUser (user);
        return ResponseEntity.ok(createdUser );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-role/{userId}/{roleId}")
    public ResponseEntity<Void> assignRoleToUser (@PathVariable Long userId, @PathVariable Long roleId) {
        System.out.println("Попытка назначения роли пользователю с ID: " + userId + " и ролью с ID: " + roleId);
        adminService.assignRoleToUser (userId, roleId);
        return ResponseEntity.ok().build();
    }

    private Long getFacultyId(FacultyType facultyType) {
        return switch (facultyType) {
            case SCIENCE -> 5L;
            case ARTS -> 4L;
            case ENGINEERING -> 3L;
            case BUSINESS -> 2L;
            case COMMON -> 1L;
            default -> throw new IllegalArgumentException("Unknown faculty type: " + facultyType);
        };
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<LibraryUser > users = adminService.getAllUsers();

        // Преобразование LibraryUser  в UserDTO
        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRoles(user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList()));
                    // Получаем только факультеты, связанные с пользователем
                    dto.setFaculties(user.getFaculties().stream()
                            .map(faculty -> {
                                Map<String, Object> facultyMap = new HashMap<>();
                                facultyMap.put("facultyId", faculty.getFacultyId());
                                facultyMap.put("type", faculty.getType().getDisplayName());
                                return facultyMap;
                            })
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        // Получение списка всех факультетов
        List<Map<String, Object>> faculties = Arrays.stream(FacultyType.values())
                .map(facultyType -> {
                    Map<String, Object> facultyMap = new HashMap<>();
                    facultyMap.put("facultyId", getFacultyId(facultyType));
                    facultyMap.put("type", facultyType.getDisplayName());
                    return facultyMap;
                })
                .collect(Collectors.toList());

        // Создание ответа
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        response.put("faculties", faculties);

        return ResponseEntity.ok(response);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-faculty/{userId}/{facultyId}")
    public ResponseEntity<Void> assignFacultyToUser (@PathVariable Long userId, @PathVariable Long facultyId) {
        adminService.assignFacultyToUser (userId, facultyId);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('ADMIN')") // Ограничение доступа для администраторов
    @PostMapping("/add-book")
    public ResponseEntity<Book> addBook(@RequestParam String title, @RequestParam Long authorId) {
        Book book = new Book();
        book.setTitle(title);
        // Установите другие свойства книги, если нужно

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LibraryUser  addedBy = (LibraryUser ) authentication.getPrincipal(); // Предполагается, что вы используете LibraryUser  как UserDetails

        // Сохраняем книгу с записью о добавлении
        Book savedBook = bookService.uploadBook(book, addedBy);

        return ResponseEntity.ok(savedBook);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-roles")
    public ResponseEntity<Void> updateRoles(@RequestBody List<UserRoleUpdateDTO> updates) {
        adminService.updateUserRoles(updates);
        return ResponseEntity.ok().build();
    }


}