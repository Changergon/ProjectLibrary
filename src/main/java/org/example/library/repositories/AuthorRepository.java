package org.example.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.library.models.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByFirstNameAndLastName(String firstName, String lastName);
}
