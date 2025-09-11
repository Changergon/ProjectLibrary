package org.example.library.repositories;

import org.example.library.models.PhysicalCopy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhysicalCopyRepository extends JpaRepository<PhysicalCopy, Long> {
    @Override
    @EntityGraph(attributePaths = {"book"})
    List<PhysicalCopy> findAll();

    @EntityGraph(attributePaths = {"book"})
    List<PhysicalCopy> findByAvailable(boolean available);
}
