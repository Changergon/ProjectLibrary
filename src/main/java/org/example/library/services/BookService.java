package org.example.library.services;

import org.example.library.domain.Book;
import org.example.library.dto.BookRequest;
import org.example.library.dto.BookResponse;
import org.example.library.dto.PageBookResponse;
import org.example.library.exception.ResourceNotFoundException;
import org.example.library.repositories.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public PageBookResponse getBooks(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage;
        if (search != null && !search.isEmpty()) {
            bookPage = bookRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        List<BookResponse> bookResponses = bookPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());

        PageBookResponse pageBookResponse = new PageBookResponse();
        pageBookResponse.setContent(bookResponses);
        pageBookResponse.setTotalElements(bookPage.getTotalElements());
        pageBookResponse.setTotalPages(bookPage.getTotalPages());
        pageBookResponse.setCurrentPage(bookPage.getNumber());
        pageBookResponse.setPageSize(bookPage.getSize());

        return pageBookResponse;
    }

    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToBookResponse(book);
    }

    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book();
        BeanUtils.copyProperties(bookRequest, book);
        book = bookRepository.save(book);
        return convertToBookResponse(book);
    }

    public BookResponse updateBook(UUID id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        BeanUtils.copyProperties(bookRequest, book);
        book = bookRepository.save(book);
        return convertToBookResponse(book);
    }

    public void deleteBook(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    private BookResponse convertToBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        BeanUtils.copyProperties(book, bookResponse);
        return bookResponse;
    }
}
