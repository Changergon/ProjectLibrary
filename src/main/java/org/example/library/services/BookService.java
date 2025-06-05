package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.exceptions.ForbiddenAccessException;
import org.example.library.exceptions.ResourceNotFoundException;
import org.example.library.exceptions.UnauthorizedAccessException;
import org.example.library.models.*;
import org.example.library.models.DTO.BookDTO;
import org.example.library.repositories.BookRatingRepository;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.EbookRepository;
import org.example.library.repositories.BookEntryRepository; // Импортируйте BookEntryRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private EbookRepository ebookRepository;

    @Autowired
    private BookEntryRepository bookEntryRepository; // Добавьте BookEntryRepository

    @Autowired
    private FacultyService facultyService; // Внедряем FacultyService

    public Page<Book> searchBooksByTitleAndAuthor(String title, String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingAndBookAuthors_Author_LastNameContaining(title, author, pageable);
    }

    public Page<Book> searchBooksByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContaining(title, pageable);
    }

    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByBookAuthors_Author_LastNameContaining(author, pageable);
    }

    public Page<Book> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingOrBookAuthors_Author_FirstNameContainingOrBookAuthors_Author_LastNameContaining(query, pageable);
    }

    private Long getCurrentUserId() {
        var userDetails = customUserDetailsService.getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }



    public Book getBookById(Long bookId) {
        logger.info("Fetching book with ID: {}", bookId);
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
    }

    public void downloadEbook(Long ebookId) {
        logger.info("Downloading ebook with ID: {}", ebookId);
        Ebook ebook = ebookRepository.findById(ebookId).orElse(null);
        if (ebook != null && ebook.getBook() != null) {
            logger.info("Ebook found: {}", ebook.getBook().getTitle());
            // Логика для скачивания
        } else {
            logger.warn("Ebook with ID {} not found", ebookId);
        }
    }

    public Book uploadBook(Book book, LibraryUser addedBy) {
        logger.info("Uploading book: {}", book.getTitle());

        // Сохранение книги
        Book savedBook = bookRepository.save(book);

        // Создание записи о добавлении книги
        BookEntry bookEntry = new BookEntry();
        bookEntry.setBook(savedBook);
        bookEntry.setAddedBy(addedBy);
        bookEntry.setAddedAt(LocalDateTime.now());

        // Сохранение записи о добавлении
        bookEntryRepository.save(bookEntry); // Теперь это будет работать

        return savedBook;
    }

    public void deleteBook(Long bookId) {
        logger.info("Deleting book with ID: {}", bookId);
        bookRepository.deleteById(bookId);
    }

    public void saveEbook(Ebook ebook) {
        if (ebook.getBook() != null) {
            logger.info("Saving ebook for book: {}", ebook.getBook().getTitle());
        } else {
            logger.info("Saving ebook with no associated book.");
        }
        ebookRepository.save(ebook);
    }

    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Создаем объект Pageable
        return bookRepository.findAll(pageable); // Используем пагинацию в репозитории
    }

    public List<Book> getBooksByFaculty(Faculty faculty) {
        String facultyName = facultyService.getFacultyDisplayName(faculty); // Используем FacultyService
        logger.info("Fetching books for faculty: {}", facultyName);
        return bookRepository.findByFaculty(faculty);
    }

    public List<BookDTO> getBooksForUser (LibraryUser  user) {
        logger.info("Fetching books for user: {}", user.getUsername());
        logger.info("User  faculties: {}", user.getFaculties());

        Set<Faculty> userFaculties = user.getFaculties();

        // Разделяем факультеты на обычные и общедоступные
        Set<Faculty> regularFaculties = userFaculties.stream()
                .filter(f -> f.getType() != FacultyType.COMMON)
                .collect(Collectors.toSet());

        boolean hasCommonFaculty = userFaculties.stream()
                .anyMatch(f -> f.getType() == FacultyType.COMMON);

        List<Book> books = new ArrayList<>();

        // Получаем книги для обычных факультетов
        if (!regularFaculties.isEmpty()) {
            books.addAll(bookRepository.findByFacultiesIn(regularFaculties));
        }

        // Добавляем общедоступные книги, если нужно
        if (hasCommonFaculty) {
            books.addAll(bookRepository.findCommonBooks());
        }

        // Удаляем дубликаты
        books = books.stream().distinct().collect(Collectors.toList());

        // Преобразуем книги в BookDTO и добавляем средние оценки
        return books.stream()
                .map(book -> convertToDTO(book, user.getUserId())) // Используем ваш метод преобразования
                .collect(Collectors.toList());
    }



    public boolean isBookAddedByUser (Long bookId, String username) {
        Book book = getBookById(bookId);
        return book.getEntry().getAddedBy().getUsername().equals(username);
    }



    public void updateBook(Book book) {
        // Здесь вы можете добавить логику для проверки, является ли пользователь преподавателем и добавил ли он эту книгу
        bookRepository.save(book);
    }

    public Page<Book> getBooksByUserId(Long userId, int page, int size) {
        logger.info("Fetching books added by user with ID: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAddedById(userId, pageable);
    }



    public double calculateAverageRating(Long bookId) {
        List<BookRating> ratings = bookRatingRepository.findByBookBookId(bookId);
        if (ratings.isEmpty()) return 0;
        return ratings.stream().mapToInt(BookRating::getRating).average().orElse(0);
    }

    public Integer getUserRating(Long bookId, Long userId) {
        if (userId == null) return null;
        return bookRatingRepository.findByBookBookIdAndUserUserId(bookId, userId)
                .map(BookRating::getRating)
                .orElse(null);
    }


    public BookDTO convertToDTO(Book book, Long currentUserId) {
        List<String> authorNames = book.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getFirstName() + " " + bookAuthor.getAuthor().getLastName())
                .collect(Collectors.toList());

        Long addedById = null;
        Optional<BookEntry> bookEntryOpt = findBookEntryByBook(book); // Вызов метода текущего класса
        if (bookEntryOpt.isPresent() && bookEntryOpt.get().getAddedBy() != null) {
            addedById = bookEntryOpt.get().getAddedBy().getId();
        }

        return new BookDTO(
                book.getBookId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getDescription(),
                book.getPublisher(),
                book.getStatus().name(),
                authorNames,
                calculateAverageRating(book.getBookId()),
                getUserRating(book.getBookId(), currentUserId),
                addedById
        );
    }



    public BookEntry getBookEntryByBook(Book book) {
        return bookEntryRepository
                .findByBook(book);
    }

    public BookDTO editBook(Long id, Long currentUserId) {
        Book book = getBookById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }

        LibraryUser  currentUser  = customUserDetailsService.getUserById(currentUserId);
        if (currentUser  == null) {
            throw new UnauthorizedAccessException("User not found");
        }

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isTeacher = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER"));

        if (isAdmin) {
            // Администратор может редактировать любую книгу
            return convertToDTO(book, currentUserId); // передайте currentUser Id

        }

        if (isTeacher) {
            // Проверяем, что текущий пользователь добавил эту книгу
            BookEntry bookEntry = getBookEntryByBook(book);
            if (bookEntry == null || !bookEntry.getAddedBy().getId().equals(currentUserId)) {
                throw new ForbiddenAccessException("You do not have permission to edit this book");
            }
            return convertToDTO(book, currentUserId); // передайте currentUser Id

        }

        // Для других ролей доступ запрещён
        throw new ForbiddenAccessException("Access denied");
    }

    public Page<Book> getBooksForEditing(Long userId, int page, int size) {
        LibraryUser  currentUser  = customUserDetailsService.getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        if (currentUser .hasRole("ADMIN")) {
            return bookRepository.findAll(pageable); // Возвращаем все книги для администраторов
        } else {
            return bookRepository.findByAddedById(userId, pageable); // Возвращаем только книги, добавленные пользователем
        }
    }

    public Optional<BookEntry> findBookEntryByBook(Book book) {
        return Optional.ofNullable(bookEntryRepository.findByBook(book));
    }

    public Page<Book> getBooksByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByBookAuthors_Author_AuthorId(authorId, pageable);
    }
}
