package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.Book;
import org.example.library.models.PhysicalCopy;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.PhysicalCopyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhysicalCopyService {

    private static final Logger logger = LoggerFactory.getLogger(PhysicalCopyService.class);

    private final PhysicalCopyRepository physicalCopyRepository;
    private final BookRepository bookRepository;

    @Autowired
    public PhysicalCopyService(PhysicalCopyRepository physicalCopyRepository, BookRepository bookRepository) {
        this.physicalCopyRepository = physicalCopyRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public PhysicalCopy addPhysicalCopy(Long bookId, int rowNumber, int shelfNumber, int positionNumber) {
        logger.info("Adding physical copy for book ID: {}", bookId);
        // bookRepository.findById is already optimized and throws EntityNotFoundException
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot add physical copy. Book not found with id: " + bookId));

        PhysicalCopy copy = new PhysicalCopy();
        copy.setBook(book);
        copy.setAvailable(true);
        copy.setRowNumber(rowNumber);
        copy.setShelfNumber(shelfNumber);
        copy.setPositionNumber(positionNumber);

        PhysicalCopy savedCopy = physicalCopyRepository.save(copy);
        logger.info("Successfully added physical copy with ID: {} for book ID: {}", savedCopy.getCopyId(), bookId);
        return savedCopy;
    }
}
