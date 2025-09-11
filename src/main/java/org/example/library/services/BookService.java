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
import org.example.library.repositories.BookEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final EbookRepository ebookRepository;
    private final BookEntryRepository bookEntryRepository;
    private final FacultyService facultyService;

    @Autowired
    public BookService(BookRepository bookRepository, BookRatingRepository bookRatingRepository, CustomUserDetailsService customUserDetailsService, EbookRepository ebookRepository, BookEntryRepository bookEntryRepository, FacultyService facultyService) {
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.ebookRepository = ebookRepository;
        this.bookEntryRepository = bookEntryRepository;
        this.facultyService = facultyService;
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooksByTitleAndAuthor(String title, String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContainingAndBookAuthors_Author_LastNameContaining(title, author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooksByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByTitleContaining(title, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooksByAuthor(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByBookAuthors_Author_LastNameContaining(author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (query == null || query.isBlank()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.findByTitleContainingOrBookAuthors_Author_FirstNameContainingOrBookAuthors_Author_LastNameContaining(query, pageable);
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long bookId) {
        logger.info("Fetching book with ID: {}", bookId);
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + bookId));
    }

    @Transactional
    public void downloadEbook(Long ebookId) {
        logger.info("Downloading ebook with ID: {}", ebookId);
        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook with ID " + ebookId + " not found"));
        if (ebook.getBook() != null) {
            logger.info("Ebook found: {}. Starting download logic...", ebook.getBook().getTitle());
            // Placeholder for actual download logic
        } else {
            logger.warn("Ebook with ID {} is not associated with any book.", ebookId);
        }
    }

    @Transactional
    public Book uploadBook(Book book, LibraryUser addedBy) {
        logger.info("Uploading book: {}", book.getTitle());
        Book savedBook = bookRepository.save(book);

        BookEntry bookEntry = new BookEntry();
        bookEntry.setBook(savedBook);
        bookEntry.setAddedBy(addedBy);
        bookEntry.setAddedAt(LocalDateTime.now());
        bookEntryRepository.save(bookEntry);

        return savedBook;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        logger.info("Deleting book with ID: {}", bookId);
        bookRepository.deleteById(bookId);
    }

    @Transactional
    public void saveEbook(Ebook ebook) {
        logger.info("Saving ebook for book: {}", ebook.getBook() != null ? ebook.getBook().getTitle() : "N/A");
        ebookRepository.save(ebook);
    }

    @Transactional
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getBooksByFaculty(Faculty faculty) {
        String facultyName = facultyService.getFacultyDisplayName(faculty);
        logger.info("Fetching books for faculty: {}", facultyName);
        return bookRepository.findByFaculty(faculty);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getBooksForUser(LibraryUser user) {
        logger.info("Fetching books for user: {}", user.getUsername());
        Set<Faculty> userFaculties = user.getFaculties();

        Set<Faculty> regularFaculties = userFaculties.stream()
                .filter(f -> f.getType() != FacultyType.COMMON)
                .collect(Collectors.toSet());

        List<Book> books = new ArrayList<>();
        if (!regularFaculties.isEmpty()) {
            books.addAll(bookRepository.findByFacultiesIn(regularFaculties));
        }

        books.addAll(bookRepository.findCommonBooks());

        List<Book> distinctBooks = books.stream().distinct().collect(Collectors.toList());

        return distinctBooks.stream()
                .map(book -> convertToDTO(book, user.getUserId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isBookAddedByUser(Long bookId, String username) {
        return bookRepository.findById(bookId)
                .map(Book::getEntry)
                .map(BookEntry::getAddedBy)
                .map(LibraryUser::getUsername)
                .map(name -> name.equals(username))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<Book> getBooksByUserId(Long userId, int page, int size) {
        logger.info("Fetching books added by user with ID: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByAddedById(userId, pageable);
    }

    @Transactional(readOnly = true)
    public BookDTO editBook(Long bookId, Long currentUserId) {
        Book book = getBookById(bookId);
        LibraryUser currentUser = customUserDetailsService.getUserById(currentUserId);

        if (currentUser == null) {
            throw new UnauthorizedAccessException("User not found");
        }

        if (currentUser.hasRole("ADMIN")) {
            return convertToDTO(book, currentUserId);
        }

        if (currentUser.hasRole("TEACHER")) {
            BookEntry bookEntry = getBookEntryByBook(book);
            if (bookEntry == null || !bookEntry.getAddedBy().getId().equals(currentUserId)) {
                throw new ForbiddenAccessException("You do not have permission to edit this book");
            }
            return convertToDTO(book, currentUserId);
        }

        throw new ForbiddenAccessException("Access denied");
    }

    @Transactional(readOnly = true)
    public Page<Book> getBooksForEditing(Long userId, int page, int size) {
        LibraryUser currentUser = customUserDetailsService.getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        if (currentUser.hasRole("ADMIN")) {
            return bookRepository.findAll(pageable);
        } else {
            return bookRepository.findByAddedById(userId, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Book> getBooksByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByBookAuthors_Author_AuthorId(authorId, pageable);
    }

    @Transactional(readOnly = true)
    public BookEntry getBookEntryByBook(Book book) {
        return bookEntryRepository.findByBook(book);
    }

    @Transactional(readOnly = true)
    public Optional<BookEntry> findBookEntryByBook(Book book) {
        return Optional.ofNullable(bookEntryRepository.findByBook(book));
    }

    public BookDTO convertToDTO(Book book, Long currentUserId) {
        List<String> authorNames = book.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getFirstName() + " " + bookAuthor.getAuthor().getLastName())
                .collect(Collectors.toList());

        Long addedById = Optional.ofNullable(book.getEntry()).map(BookEntry::getAddedBy).map(LibraryUser::getId).orElse(null);

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

    @Transactional(readOnly = true)
    public double calculateAverageRating(Long bookId) {
        List<BookRating> ratings = bookRatingRepository.findByBookBookId(bookId);
        if (ratings.isEmpty()) return 0;
        return ratings.stream().mapToInt(BookRating::getRating).average().orElse(0);
    }

    @Transactional(readOnly = true)
    public Integer getUserRating(Long bookId, Long userId) {
        if (userId == null) return null;
        return bookRatingRepository.findByBookBookIdAndUserUserId(bookId, userId)
                .map(BookRating::getRating)
                .orElse(null);
    }
}
