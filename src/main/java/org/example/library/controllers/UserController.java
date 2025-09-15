package org.example.library.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.library.models.Book;
import org.example.library.models.DTO.BookDTO;
import org.example.library.models.DTO.UserDTO;
import org.example.library.models.DTO.UserLoginDTO;
import org.example.library.models.DTO.UserRegistrationDTO;
import org.example.library.models.LibraryUser ;
import org.example.library.models.Role;
import org.example.library.services.BookService;
import org.example.library.services.FacultyService;
import org.example.library.services.UserService;
import org.example.library.exceptions.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.example.library.repositories.RoleRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser (@Valid @RequestBody UserRegistrationDTO userDto) {
        try {
            LibraryUser  registeredUser  = userService.registerUser (userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
            return ResponseEntity.status(201).body("Пользователь успешно зарегистрирован");
        } catch (UserAlreadyExistsException e) {
            logger.error("Ошибка регистрации: {}", e.getMessage());
            return ResponseEntity.status(400).body("Ошибка регистрации: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка сервера: {}", e.getMessage());
            return ResponseEntity.status(500).body("Ошибка сервера: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        logger.info("Попытка входа пользователя: {}", userLoginDTO.getUsername());

        // Очистка старой сессии
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate(); // Уничтожаем старую сессию
        }

        LibraryUser  user = userService.findByUsername(userLoginDTO.getUsername());
        if (user == null) {
            logger.error("Пользователь не найден: {}", userLoginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Пользователь не найден"));
        }

        boolean passwordMatches = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPasswordHash());
        if (!passwordMatches) {
            logger.error("Ошибка аутентификации: Неверные учетные данные пользователя");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Неверные учетные данные"));
        }

        // Аутентификация
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUserId", user.getUserId()); // Сохраните ID пользователя в сессии
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        logger.info("Пользователь {} успешно аутентифицирован", userLoginDTO.getUsername());

        // Получаем роли пользователя
        var roles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());

        // Возвращаем роли и сообщение
        Map<String, Object> response = new HashMap<>();
        response.put("roles", roles);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/checkSession")
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Map<String, Object> response = new HashMap<>();
        response.put("sessionActive", session != null && session.getAttribute("currentUserId") != null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            logger.info("Получение текущего пользователя: {}", username);
            LibraryUser user = userService.findByUsername(username);
            if (user != null) {
                UserDTO userDTO = convertToUserDTO(user);
                return ResponseEntity.ok(userDTO);
            } else {
                logger.warn("Пользователь не найден: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        logger.warn("Пользователь не аутентифицирован");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }


    // Метод для преобразования LibraryUser  в UserDTO
    private UserDTO convertToUserDTO(LibraryUser  user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        // Получаем роли пользователя
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        userDTO.setRoles(roleNames);

        // Получаем факультеты
        List<Map<String, Object>> faculties = user.getFaculties().stream()
                .map(faculty -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("facultyId", faculty.getFacultyId());
                    String facultyName = facultyService.getFacultyDisplayName(faculty);
                    map.put("facultyName", facultyName != null ? facultyName : "");
                    return map;
                })
                .collect(Collectors.toList());

        userDTO.setFaculties(faculties);

        return userDTO;
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Начало процесса выхода");

        // 1. Получаем текущую аутентификацию
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Выход пользователя: {}", auth.getName());

            // 2. Очищаем контекст безопасности
            new SecurityContextLogoutHandler().logout(request, response, auth);

            // 3. Инвалидируем сессию
            HttpSession session = request.getSession(false);
            if (session != null) {
                logger.info("Инвалидация сессии с ID: {}", session.getId());
                session.invalidate();
            }

            // 4. Очищаем контекст
            SecurityContextHolder.clearContext();

            // 5. Удаляем cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        logger.info("Удаление cookie JSESSIONID");
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }

            // 6. Добавляем дополнительные заголовки для очистки кэша
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            logger.info("Выход завершен успешно");
            return ResponseEntity.ok().body("Вы успешно вышли из системы");
        } else {
            logger.info("Нет активной аутентификации для выхода");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не удалось выйти");
        }
    }

    @PostMapping("/{userId}/bookshelf/{bookId}")
    public ResponseEntity<Void> addBookToShelf(@PathVariable Long userId, @PathVariable Long bookId) {
        try {
            userService.addBookToShelf(userId, bookId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/bookshelf/{bookId}")
    public ResponseEntity<Void> removeBookFromShelf(@PathVariable Long userId, @PathVariable Long bookId) {
        try {
            userService.removeBookFromShelf(userId, bookId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/bookshelf")
    public ResponseEntity<List<BookDTO>> getBookshelf(@PathVariable Long userId) {
        try {
            Set<Book> bookshelf = userService.getBookshelf(userId);
            List<BookDTO> bookDTOs = bookshelf.stream()
                    .map(book -> bookService.convertToDTO(book, userId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
