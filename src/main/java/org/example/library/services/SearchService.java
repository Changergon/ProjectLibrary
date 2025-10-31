package org.example.library.services;

import org.example.library.domain.Book;
import org.example.library.domain.Journal;
import org.example.library.dto.BookResponse;
import org.example.library.dto.JournalResponse;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.JournalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchService {

    private final BookRepository bookRepository;
    private final JournalRepository journalRepository;

    public SearchService(BookRepository bookRepository, JournalRepository journalRepository) {
        this.bookRepository = bookRepository;
        this.journalRepository = journalRepository;
    }

    public Map<String, Page<?>> search(String query, Pageable pageable) {
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(query, pageable);
        Page<Journal> journals = journalRepository.findByTitleContainingIgnoreCase(query, pageable);

        Map<String, Page<?>> results = new HashMap<>();
        results.put("books", books.map(this::convertToBookResponse));
        results.put("journals", journals.map(this::convertToJournalResponse));

        return results;
    }

    private BookResponse convertToBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        BeanUtils.copyProperties(book, bookResponse);
        return bookResponse;
    }

    private JournalResponse convertToJournalResponse(Journal journal) {
        JournalResponse journalResponse = new JournalResponse();
        BeanUtils.copyProperties(journal, journalResponse);
        return journalResponse;
    }
}
