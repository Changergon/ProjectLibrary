package org.example.library.repositories;

import org.example.library.domain.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {

    Page<Journal> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
