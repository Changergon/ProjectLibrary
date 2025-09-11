package org.example.library.repositories;

import org.example.library.models.Faculty;
import org.example.library.models.FacultyType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @EntityGraph(attributePaths = {"books", "users"})
    List<Faculty> findByType(FacultyType type);

    @Override
    @EntityGraph(attributePaths = {"books", "users"})
    Optional<Faculty> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"books", "users"})
    List<Faculty> findAll();
}
