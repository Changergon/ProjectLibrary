package org.example.library.repositories;

import org.example.library.models.LibraryUser ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryUserRepository extends JpaRepository<LibraryUser , Long> {
    LibraryUser  findByUsername(String username);
    LibraryUser  findByEmail(String email);
}

