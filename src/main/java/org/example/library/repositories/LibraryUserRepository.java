package org.example.library.repositories;

import org.example.library.models.Book;
import org.example.library.models.LibraryUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {

    @EntityGraph(attributePaths = {"roles", "faculties"})
    LibraryUser findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "faculties"})
    LibraryUser findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"roles", "faculties"})
    Optional<LibraryUser> findById(Long userId);

    @Override
    @EntityGraph(attributePaths = {"roles", "faculties"})
    List<LibraryUser> findAll();

    List<LibraryUser> findAllByLastReadBook(Book book);
}
