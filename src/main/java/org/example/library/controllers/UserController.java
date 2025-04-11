package org.example.library.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.library.models.DTO.UserLoginDTO;
import org.example.library.models.DTO.UserRegistrationDTO;
import org.example.library.models.LibraryUser ;
import org.example.library.models.Role;
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
import org.springframework.web.bind.annotation.*;
import org.example.library.repositories.RoleRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public ResponseEntity<LibraryUser > getCurrentUser () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            LibraryUser user = userService.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }
}
