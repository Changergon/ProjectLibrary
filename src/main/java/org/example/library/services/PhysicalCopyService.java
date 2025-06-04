package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.models.PhysicalCopy;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.PhysicalCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhysicalCopyService {

    @Autowired
    private PhysicalCopyRepository physicalCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    public PhysicalCopy addPhysicalCopy(Long bookId, int rowNumber, int shelfNumber, int positionNumber) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена с id: " + bookId));

        PhysicalCopy copy = new PhysicalCopy();
        copy.setBook(book);
        copy.setAvailable(true);
        copy.setRowNumber(rowNumber);
        copy.setShelfNumber(shelfNumber);
        copy.setPositionNumber(positionNumber);

        return physicalCopyRepository.save(copy);
    }
}
