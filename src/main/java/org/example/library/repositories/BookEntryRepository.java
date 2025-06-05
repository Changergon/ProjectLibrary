package org.example.library.repositories;

import org.example.library.models.Book;
import org.example.library.models.BookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {
    BookEntry findByBook(Book book);
}

