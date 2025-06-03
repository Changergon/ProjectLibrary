package org.example.library.services;

import jakarta.transaction.Transactional;
import org.example.library.models.Book;
import org.example.library.models.BookRating;
import org.example.library.models.LibraryUser;
import org.example.library.repositories.BookRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookRatingService {

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addOrUpdateRating(Long bookId, int rating, String username) {
        LibraryUser user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new RuntimeException("Книга не найдена");
        }

        Optional<BookRating> existingRatingOpt = bookRatingRepository.findByBookBookIdAndUserUserId(bookId, user.getUserId());

        BookRating bookRating = existingRatingOpt.orElse(new BookRating());
        bookRating.setBook(book);
        bookRating.setUser(user);
        bookRating.setRating(rating);

        bookRatingRepository.save(bookRating);

        System.out.println("Сохранена оценка: " + rating + " для книги " + bookId + " пользователем " + username);
    }

}
