package org.example.library.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.library.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    @EntityGraph(attributePaths = {"bookAuthors"})
    Author findByFirstNameAndLastName(String firstName, String lastName);

    @Override
    @EntityGraph(attributePaths = {"bookAuthors"})
    List<Author> findAll();

    @Override
    @EntityGraph(attributePaths = {"bookAuthors"})
    Optional<Author> findById(Long id);
}
