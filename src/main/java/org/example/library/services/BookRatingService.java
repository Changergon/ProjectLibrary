package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.Book;
import org.example.library.models.BookRating;
import org.example.library.models.LibraryUser;
import org.example.library.repositories.BookRatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookRatingService {

    private static final Logger logger = LoggerFactory.getLogger(BookRatingService.class);

    private final BookRatingRepository bookRatingRepository;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BookRatingService(BookRatingRepository bookRatingRepository, BookService bookService, UserService userService) {
        this.bookRatingRepository = bookRatingRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    public void addOrUpdateRating(Long bookId, int rating, String username) {
        logger.info("Attempting to add/update rating for book ID: {} by user: {}", bookId, username);

        LibraryUser user = userService.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }

        // getBookById already throws EntityNotFoundException if book is not found
        Book book = bookService.getBookById(bookId);

        Optional<BookRating> existingRatingOpt = bookRatingRepository.findByBookBookIdAndUserUserId(bookId, user.getId());

        BookRating bookRating = existingRatingOpt.orElse(new BookRating());
        bookRating.setBook(book);
        bookRating.setUser(user);
        bookRating.setRating(rating);

        bookRatingRepository.save(bookRating);

        logger.info("Saved rating: {} for book ID: {} by user: {}", rating, bookId, username);
    }
}
