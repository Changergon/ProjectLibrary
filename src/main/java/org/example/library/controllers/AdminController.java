package org.example.library.controllers;

import org.example.library.models.*;
import org.example.library.models.DTO.UserDTO;
import org.example.library.models.DTO.UserRoleUpdateDTO;
import org.example.library.repositories.AuthorRepository;
import org.example.library.repositories.LibraryUserRepository;
import org.example.library.services.AdminService;
import org.example.library.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BookService bookService;
    private final AuthorRepository authorRepository;
    private final LibraryUserRepository userRepository;

    public AdminController(AdminService adminService, BookService bookService, AuthorRepository authorRepository, LibraryUserRepository userRepository) {
        this.adminService = adminService;
        this.bookService = bookService;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<LibraryUser> createUser(@RequestBody LibraryUser user) {
        LibraryUser createdUser = adminService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-role/{userId}/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        adminService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    private Long getFacultyId(FacultyType facultyType) {
        return switch (facultyType) {
            case COMMON -> 1L;
            case ARTS -> 2L;
            case ENGINEERING -> 3L;
            case BUSINESS -> 4L;
            case SCIENCE -> 5L;
        };
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<LibraryUser> users = adminService.getAllUsers();

        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRoles(user.getRoles().stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList()));
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

        List<Map<String, Object>> faculties = Arrays.stream(FacultyType.values())
                .map(facultyType -> {
                    Map<String, Object> facultyMap = new HashMap<>();
                    facultyMap.put("facultyId", getFacultyId(facultyType));
                    facultyMap.put("type", facultyType.getDisplayName());
                    return facultyMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        response.put("faculties", faculties);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-faculty/{userId}/{facultyId}")
    public ResponseEntity<Void> assignFacultyToUser(@PathVariable Long userId, @PathVariable Long facultyId) {
        adminService.assignFacultyToUser(userId, facultyId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-book")
    public ResponseEntity<Book> addBook(@RequestParam String title, @RequestParam Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

        Book book = new Book();
        book.setTitle(title);
        book.setStatus(BookStatus.AVAILABLE);

        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setBook(book);
        bookAuthor.setAuthor(author);
        book.setBookAuthors(new ArrayList<>(Collections.singletonList(bookAuthor)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        LibraryUser addedBy = userRepository.findByUsername(currentUsername);

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
