package org.example.library.repositories;

import org.example.library.models.BookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEntryRepository extends JpaRepository<BookEntry, Long> {
    // Здесь вы можете добавить дополнительные методы для поиска, если это необходимо
}
