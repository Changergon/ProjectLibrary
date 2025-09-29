package org.example.library.controllers;

import org.example.library.models.Book;
import org.example.library.models.LibraryUser ;
import org.example.library.models.Role;
import org.example.library.services.BookService; // Импортируйте BookService
import org.example.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    @Autowired
    private UserService userService; // Сервис для работы с пользователями

    @Autowired
    private BookService bookService; // Сервис для работы с книгами

    @GetMapping("/")
    public String home(Model model) {
        return "index"; // Возврат имени шаблона для отображения домашней страницы
    }

    @GetMapping("/books")
    public String viewBooks(Model model, Principal principal) {
        addCurrentUserToModel(model);
        return "books";
    }

    @GetMapping("/bookshelf")
    public String bookshelf(Model model) {
        addCurrentUserToModel(model);
        return "bookshelf";
    }

    @GetMapping("/my-rentals")
    @PreAuthorize("isAuthenticated()")
    public String myRentals(Model model) {
        addCurrentUserToModel(model);
        return "my-rentals";
    }

    @GetMapping("/books/read")
    public String readBooks(Model model) {
        return "reader"; // Возврат имени шаблона для отображения списка книг
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register"; // Возврат имени шаблона для страницы регистрации
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login"; // Возврат имени шаблона для страницы авторизации
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {
        addCurrentUserToModel(model);
        return "admin";
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/librarian-dashboard")
    public String librarianDashboard(Model model) {
        addCurrentUserToModel(model);
        return "librarian-dashboard";
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/add-book")
    public String addBook(Model model, Principal principal) {
        addCurrentUserToModel(model);
        return "addbook";
    }

    @GetMapping("/rent-books")
    @PreAuthorize("isAuthenticated()")
    public String showRentBooksPage(Model model) {
        addCurrentUserToModel(model);
        return "rent-books";
    }

    @GetMapping("/add-physical-copy")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public String showAddPhysicalCopyPage() {
        return "add-physical-copy";
    }

    // Метод для добавления текущего пользователя в модель
    private void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            LibraryUser user = userService.findByUsername(username);
            model.addAttribute("currentUser", user);
        }
    }




    @GetMapping("/books-edit")
    public String editBooks(Model model, Principal principal) {
        addCurrentUserToModel(model);
        return "books-edit";
    }


    @GetMapping("/books/edit")
    public String editBook(@RequestParam("id") Long bookId, Model model, Principal principal) {
        addCurrentUserToModel(model);
        Book book = bookService.getBookById(bookId);
        model.addAttribute("book", book);
        return "editbook";
    }



}
