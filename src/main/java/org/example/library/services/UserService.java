package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.exceptions.UserAlreadyExistsException;
import org.example.library.models.*;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.FacultyRepository;
import org.example.library.repositories.LibraryUserRepository;
import org.example.library.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final LibraryUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final FacultyRepository facultyRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(LibraryUserRepository userRepository, RoleRepository roleRepository, BookRepository bookRepository, FacultyRepository facultyRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LibraryUser registerUser(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username) != null) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        LibraryUser user = new LibraryUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        Role studentRole = roleRepository.findByRoleName("STUDENT");
        if (studentRole == null) {
            logger.error("Default role 'STUDENT' not found in the database!");
            // Or save it if it doesn't exist
            studentRole = roleRepository.save(new Role("STUDENT"));
        }
        user.setRoles(new HashSet<>(Collections.singletonList(studentRole)));

        // Find or create the COMMON faculty
        Faculty commonFaculty = facultyRepository.findByType(FacultyType.COMMON).stream().findFirst().orElseGet(() -> {
            logger.info("COMMON faculty not found, creating a new one.");
            Faculty newFaculty = new Faculty();
            newFaculty.setType(FacultyType.COMMON);
            return facultyRepository.save(newFaculty);
        });

        user.getFaculties().add(commonFaculty);

        logger.info("Registering new user: {}", username);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LibraryUser authenticateUser(String username, String password, String role) {
        LibraryUser user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Authentication failed: User '{}' not found", username);
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Authentication failed: Invalid password for user '{}'", username);
            return null;
        }

        if (!user.hasRole(role)) {
            logger.warn("Authentication failed: User '{}' does not have the required role '{}'", username, role);
            return null;
        }

        logger.info("User '{}' authenticated successfully with role '{}'", username, role);
        return user;
    }

    @Transactional(readOnly = true)
    public LibraryUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public LibraryUser findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Transactional
    public void updateLastReadBook(Long userId, Long bookId) {
        logger.info("Updating last read book for user ID: {}", userId);
        LibraryUser user = findById(userId); // Use the local, optimized method
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
        user.setLastReadBook(book);
        userRepository.save(user);
        logger.info("Last read book for user {} updated to book with ID: {}", userId, bookId);
    }

    @Transactional
    public void addBookToShelf(Long userId, Long bookId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));

        user.getBookshelf().add(book);
        logger.info("Book '{}' added to bookshelf for user '{}'", book.getTitle(), user.getUsername());
    }

    @Transactional
    public void removeBookFromShelf(Long userId, Long bookId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));

        user.getBookshelf().remove(book);
        logger.info("Book '{}' removed from bookshelf for user '{}'", book.getTitle(), user.getUsername());
    }

    @Transactional(readOnly = true)
    public Set<Book> getBookshelf(Long userId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        // Eagerly fetch the bookshelf to avoid LazyInitializationException
        user.getBookshelf().size(); // This forces initialization
        return user.getBookshelf();
    }
}
