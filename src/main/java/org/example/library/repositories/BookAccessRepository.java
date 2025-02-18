package org.example.library.repositories;

import org.example.library.models.BookAccess;
import org.example.library.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAccessRepository extends JpaRepository<BookAccess, Long> {
    void deleteByBook(Book book); // Метод для удаления доступа по книге
}
