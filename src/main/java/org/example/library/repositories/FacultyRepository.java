package org.example.library.repositories;

import org.example.library.models.Faculty;
import org.example.library.models.FacultyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByType(FacultyType type);

    // Измените тип параметра на Long
    Faculty findByFacultyId(Long facultyId); // Теперь это Long
}
