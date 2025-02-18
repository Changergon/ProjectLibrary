package org.example.library.repositories;

import org.example.library.models.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EbookRepository extends JpaRepository<Ebook, Long> {
}
