package org.example.library.service;

import org.example.library.domain.Book;
import org.example.library.dto.BookRequest;
import org.example.library.dto.BookResponse;
import org.example.library.exception.ResourceNotFoundException;
import org.example.library.repositories.BookRepository;
import org.example.library.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getBookById_shouldReturnBookResponse() {
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookResponse bookResponse = bookService.getBookById(bookId);

        assertEquals(bookId, bookResponse.getId());
        assertEquals("Test Book", bookResponse.getTitle());
        assertEquals("Test Author", bookResponse.getAuthor());
    }

    @Test
    void getBookById_shouldThrowResourceNotFoundException() {
        UUID bookId = UUID.randomUUID();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookId));
    }

    @Test
    void createBook_shouldReturnBookResponse() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setAuthor("Test Author");

        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse bookResponse = bookService.createBook(bookRequest);

        assertEquals("Test Book", bookResponse.getTitle());
        assertEquals("Test Author", bookResponse.getAuthor());
    }
}
