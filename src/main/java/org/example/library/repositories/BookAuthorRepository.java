package org.example.library.repositories;

import org.example.library.models.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    // Здесь могут быть дополнительные методы, если необходимо
}
