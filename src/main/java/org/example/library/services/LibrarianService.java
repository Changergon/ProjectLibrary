package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.Book;
import org.example.library.models.RentalRequest;
import org.example.library.models.RentalRequestStatus;
import org.example.library.models.BookStatus;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.RentalRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LibrarianService {

    private static final Logger logger = LoggerFactory.getLogger(LibrarianService.class);

    private final BookRepository bookRepository;
    private final RentalRequestRepository rentalRequestRepository;

    @Autowired
    public LibrarianService(BookRepository bookRepository, RentalRequestRepository rentalRequestRepository) {
        this.bookRepository = bookRepository;
        this.rentalRequestRepository = rentalRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getRentalRequests() {
        logger.info("Fetching all rental requests.");
        // This now uses the optimized findAll() from the repository
        return rentalRequestRepository.findAll();
    }

    @Transactional
    public void confirmRentalReady(Long requestId) {
        logger.info("Confirming rental request with ID: {}", requestId);
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rental request not found with ID: " + requestId));
        request.setStatus(RentalRequestStatus.APPROVED);
        rentalRequestRepository.save(request);
        logger.info("Rental request with ID: {} status updated to APPROVED", requestId);
    }

    @Transactional(readOnly = true)
    public List<Book> getRentedBooks() {
        logger.info("Fetching all rented books (status: NOT_AVAILABLE).");
        // This uses the optimized findRentedBooks() from BookRepository
        return bookRepository.findRentedBooks(BookStatus.NOT_AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<RentalRequest> getRentalPeriods() {
        logger.info("Fetching all rental periods (all rental requests).");
        // This now uses the optimized findAll() from the repository
        return rentalRequestRepository.findAll();
    }
}
