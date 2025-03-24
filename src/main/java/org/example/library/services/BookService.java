package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.*;
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
import java.util.List;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

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

    public List<Book> getBooksForUser (LibraryUser  user) {
        logger.info("Fetching books for user: {}", user.getUsername());

        if (user.getFaculties().isEmpty()) {
            logger.info("User  has no faculties, fetching common books");
            return bookRepository.findByFacultiesType(FacultyType.COMMON);
        } else {
            logger.info("Fetching books for faculties: {}", user.getFaculties());
            return bookRepository.findByFacultiesIn(user.getFaculties());
        }
    }


    public boolean isBookAddedByUser (Long bookId, String username) {
        Book book = getBookById(bookId);
        return book.getEntry().getAddedBy().getUsername().equals(username);
    }

    // Код из файла: C:\Users\Дмитрий\IdeaProjects\ProjectLibrary\src\main\java\org\example\library\services\BookService.java

    public void updateBook(Book book) {
        // Здесь вы можете добавить логику для проверки, является ли пользователь преподавателем и добавил ли он эту книгу
        bookRepository.save(book);
    }

    public List<Book> getBooksByUserId(Long userId) {
        logger.info("Fetching books added by user with ID: {}", userId);
        return bookRepository.findByAddedById(userId); // Предполагается, что у вас есть соответствующий метод в репозитории
    }


}
