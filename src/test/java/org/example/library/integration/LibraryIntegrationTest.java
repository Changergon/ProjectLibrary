package org.example.library.integration;

import org.example.library.domain.Book;
import org.example.library.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LibraryIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void testCreateAndRetrieveBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        book = bookRepository.save(book);

        assertNotNull(book.getId());

        Book retrievedBook = bookRepository.findById(book.getId()).orElse(null);
        assertNotNull(retrievedBook);
        assertEquals("Test Book", retrievedBook.getTitle());
    }
}
