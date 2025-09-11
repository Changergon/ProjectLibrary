package org.example.library.repositories;

import org.example.library.models.Book;
import org.example.library.models.BookEntry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {
    @EntityGraph(attributePaths = {"addedBy", "book"})
    BookEntry findByBook(Book book);
}
