package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.models.BookStatus;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.LibraryUserRepository;
import org.example.library.repositories.RentalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarianService {

    private final Logger logger = LoggerFactory.getLogger(LibrarianService.class);
    @Autowired
    private LibraryUserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    // Метод для получения запросов на аренду
    public List<RentalRequest> getRentalRequests() {
        return rentalRequestRepository.findAll();
    }

    public void confirmRentalReady(Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден с ID: " + requestId));
        request.setStatus(RentalRequestStatus.APPROVED); // Используем перечисление
        rentalRequestRepository.save(request);
    }

    // Метод для получения списка выданных книг
    public List<Book> getRentedBooks() {
        logger.debug("getRentedBooks");
        return bookRepository.findRentedBooks(BookStatus.NOT_AVAILABLE); // Передаем статус "Выдано"
    }

    // Метод для получения сроков аренды
    public List<RentalRequest> getRentalPeriods() {
        return rentalRequestRepository.findAll(); // Или другая логика для получения сроков
    }
}
