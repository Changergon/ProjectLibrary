package org.example.library.controllers;

import org.example.library.models.*;
import org.example.library.models.DTO.BookDTO;
import org.example.library.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final Logger logger = LoggerFactory.getLogger(BookController.class);
    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private UserService userService;

    @Autowired
    private FacultyService facultyService; // Сервис для работы с факультетами

    @Autowired
    private CustomUserDetailsService customUserDetailsService;  // Внедрение зависимости

    @Value("${file.upload-dir}") // Внедряем путь из application.properties
    private String uploadDir;


    // Получение всех книг
    @GetMapping("/all")
    public ResponseEntity<List<BookDTO>> getAllBooks(Authentication authentication) {
        LibraryUser currentUser = userService.findByUsername(authentication.getName());
        List<Book> books = bookService.getBooksForUser(currentUser); // Получаем книги для текущего пользователя
        List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }


    // Поиск книг по заголовку
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String title) {
        logger.trace("Method searchBooks with parameters: title {} ", title);
        List<Book> books = bookService.searchBooks(title);
        List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    // Получение книги по ID
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        logger.trace("Method getBookById with parameters: id {} ", id);
        Book book = bookService.getBookById(id);
        return book != null ? ResponseEntity.ok(convertToDTO(book)) : ResponseEntity.notFound().build();
    }


    // Загрузка новой книги
    @PostMapping("/upload")
    public ResponseEntity<String> uploadBook(
            @RequestParam String title,
            @RequestParam String isbn,
            @RequestParam int publicationYear,
            @RequestParam String description,
            @RequestParam String publisher,
            @RequestParam String authorFirstName,
            @RequestParam String authorLastName,
            @RequestParam MultipartFile file,
            @RequestParam List<Long> facultyIds,
            Authentication authentication) {

        LibraryUser currentUser = userService.findByUsername(authentication.getName());

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден.");
        }

        boolean isAuthorized = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("TEACHER") || role.getRoleName().equals("ADMIN"));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав для добавления книги.");
        }

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().body("Файл не должен быть пустым и должен иметь имя.");
        }

        String fileLocation;
        try {
            fileLocation = saveFile(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении файла: " + e.getMessage());
        }

        Author author = authorService.findByFirstNameAndLastName(authorFirstName, authorLastName);
        if (author == null) {
            author = new Author();
            author.setFirstName(authorFirstName);
            author.setLastName(authorLastName);
            authorService.saveAuthor(author);
        }

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setDescription(description);
        book.setPublisher(publisher);
        book.setStatus(BookStatus.AVAILABLE);

        if (facultyIds == null || facultyIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Необходимо выбрать хотя бы один факультет.");
        }

        Set<Faculty> faculties = facultyIds.stream()
                .map(facultyService::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (faculties.isEmpty()) {
            return ResponseEntity.badRequest().body("Некоторые факультеты не найдены.");
        }
        book.setFaculties(faculties);

        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setAuthor(author);
        bookAuthor.setBook(book);
        book.setBookAuthors(Collections.singletonList(bookAuthor));

        try {
            bookService.uploadBook(book, currentUser); // Передаем текущего пользователя
            Ebook ebook = new Ebook();
            ebook.setFileLocation(fileLocation);
            ebook.setBook(book);
            bookService.saveEbook(ebook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении книги: " + e.getMessage());
        }

        return ResponseEntity.ok("Книга успешно добавлена!");
    }


    // Сохранение файла на сервере
    private String saveFile(MultipartFile file) throws IOException { // Добавили throws IOException
        if (file == null || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Файл не должен быть null и должен иметь имя.");
        }
        try {
            // Создаем Path для директории загрузок
            Path uploadPath = Paths.get(uploadDir);

            // Создаем директорию, если она не существует
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Формируем полный путь к файлу
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Сохраняем файл
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString(); // Возвращаем абсолютный путь к файлу в виде строки
        } catch (IOException e) {
            throw new IOException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }


    // Получение содержимого книги
    @GetMapping("/{id}/content")
    public ResponseEntity<FileSystemResource> getBookContent(@PathVariable Long id) throws IOException {
        // Получите книгу из базы данных
        Book book = bookService.getBookById(id);
        if (book == null || book.getEbooks() == null || book.getEbooks().isEmpty()) { // Добавлена проверка на null для getEbooks()
            return ResponseEntity.notFound().build();
        }

        Ebook ebook = book.getEbooks().getFirst(); // Получаем первый eBook
        System.out.println("Путь к файлу: " + ebook.getFileLocation()); // Добавлено для отладки
        File pdfFile = new File(ebook.getFileLocation());

        if (!pdfFile.exists()) {
            System.err.println("Файл не найден по пути: " + ebook.getFileLocation()); // Лог ошибки
            return ResponseEntity.notFound().build();
        }

        // Кодируем имя файла
        String encodedFileName = URLEncoder.encode(pdfFile.getName(), StandardCharsets.UTF_8); // Используем StandardCharsets.UTF_8.toString()

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(pdfFile));
    }

    // Метод для скачивания электронной книги
    @GetMapping("/download/{id}")
    public ResponseEntity<Void> downloadEbook(@PathVariable Long id) {
        bookService.downloadEbook(id);
        return ResponseEntity.ok().build();
    }

    // Преобразование книги в DTO
    private BookDTO convertToDTO(Book book) {
        List<String> authorNames = book.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getFirstName() + " " + bookAuthor.getAuthor().getLastName())
                .collect(Collectors.toList());

        return new BookDTO(
                book.getBookId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getDescription(),
                book.getPublisher(),
                book.getStatus().name(), // Преобразуем статус в строку
                authorNames
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookDTO>> getBooksForUser(@PathVariable Long userId) {
        logger.trace("Method getBooksForUser with parameters: userId {} ", userId);
        System.out.println("Полученный userId: " + userId);

        if (userId <= 0) {
            return ResponseEntity.badRequest().body(Collections.emptyList()); // Неверный userId
        }

        LibraryUser currentUser = userService.findById(userId);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        try {
            List<Book> books = bookService.getBooksForUser(currentUser);
            List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(bookDTOs);
        } catch (Exception e) {
            // Логируем ошибку (например, через Logger)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @bookService.isBookAddedByUser (#id, principal.username))")
    @GetMapping("/edit/{id}")
    public ResponseEntity<BookDTO> editBook(@PathVariable Long id) {
        logger.trace("Method editBook with parameters: id {} ", id);
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(convertToDTO(book));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @bookService.isBookAddedByUser (#bookId, principal.username))")
    @PostMapping("/update")
    public ResponseEntity<String> updateBook(
            @RequestParam Long bookId,
            @RequestParam String title,
            @RequestParam String isbn,
            @RequestParam int publicationYear,
            @RequestParam String description,
            @RequestParam String publisher,
            @RequestParam String authorFirstName,
            @RequestParam String authorLastName,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam List<Long> facultyIds,
            Authentication authentication) {

        LibraryUser currentUser = userService.findByUsername(authentication.getName());

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден.");
        }

        boolean isAuthorized = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("TEACHER") || role.getRoleName().equals("ADMIN"));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав для обновления книги.");
        }

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        // Обновление полей книги
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setDescription(description);
        book.setPublisher(publisher);

        // Обработка файла, если он был загружен
        if (file != null && !file.isEmpty()) {
            try {
                // Удаляем старый файл, если он есть и если загружается новый
                if (book.getEbooks() != null && !book.getEbooks().isEmpty()) {
                    Ebook oldEbook = book.getEbooks().getFirst();
                    if (oldEbook != null && oldEbook.getFileLocation() != null) {
                        try {
                            Files.deleteIfExists(Paths.get(oldEbook.getFileLocation()));
                            // Рассмотрите удаление записи Ebook из БД или обновление fileLocation
                        } catch (IOException e) {
                            System.err.println("Не удалось удалить старый файл: " + oldEbook.getFileLocation() + " - " + e.getMessage());
                        }
                    }
                }

                String fileLocation = saveFile(file);
                // Если у книги уже есть Ebook, обновляем его. Иначе создаем новый.
                Ebook ebook;
                if (book.getEbooks() != null && !book.getEbooks().isEmpty()) {
                    ebook = book.getEbooks().getFirst();
                    ebook.setFileLocation(fileLocation);
                } else {
                    ebook = new Ebook();
                    ebook.setFileLocation(fileLocation);
                    ebook.setBook(book);
                    if (book.getEbooks() == null) {
                        book.setEbooks(new ArrayList<>());
                    }
                    book.getEbooks().add(ebook);
                }
                bookService.saveEbook(ebook); // Сохраняем или обновляем Ebook

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сохранении файла: " + e.getMessage());
            }
        }

        // Обновление авторов
        Author author = authorService.findByFirstNameAndLastName(authorFirstName, authorLastName);
        if (author == null) {
            author = new Author();
            author.setFirstName(authorFirstName);
            author.setLastName(authorLastName);
            authorService.saveAuthor(author);
        }
        // Обновление связи BookAuthor (если она изменилась)
        // Текущая логика предполагает одного автора на книгу, если это не так, ее нужно будет расширить
        if (book.getBookAuthors() != null && !book.getBookAuthors().isEmpty()) {
            book.getBookAuthors().getFirst().setAuthor(author);
        } else {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setAuthor(author);
            bookAuthor.setBook(book);
            if (book.getBookAuthors() == null) {
                book.setBookAuthors(new ArrayList<>());
            }
            book.getBookAuthors().add(bookAuthor);
        }


        // Обновление факультетов
        Set<Faculty> faculties = facultyIds.stream()
                .map(facultyService::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (faculties.isEmpty()) { // Может быть ситуация, когда все facultyIds невалидны
            return ResponseEntity.badRequest().body("Факультеты не найдены или не выбраны.");
        }

        book.setFaculties(faculties);
        bookService.updateBook(book); // Сохранение обновленной книги

        return ResponseEntity.ok("Книга успешно обновлена!");
    }
}

