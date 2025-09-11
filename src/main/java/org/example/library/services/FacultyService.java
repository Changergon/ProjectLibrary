package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.Faculty;
import org.example.library.models.FacultyType;
import org.example.library.repositories.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Transactional(readOnly = true)
    public Faculty findById(Long id) {
        logger.info("Fetching faculty by ID: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with ID: " + id));
    }

    @Transactional
    public Faculty save(Faculty faculty) {
        logger.info("Saving faculty with type: {}", faculty.getType());
        return facultyRepository.save(faculty);
    }

    @Transactional(readOnly = true)
    public List<Faculty> findAll() {
        logger.info("Fetching all faculties.");
        // This now uses the optimized findAll() from the repository
        return facultyRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting faculty with ID: {}", id);
        if (!facultyRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Faculty not found with ID: " + id);
        }
        facultyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Faculty> findByType(FacultyType type) {
        logger.info("Fetching faculties by type: {}", type);
        // This now uses the optimized findByType() from the repository
        return facultyRepository.findByType(type);
    }

    public String getFacultyDisplayName(Faculty faculty) {
        if (faculty != null && faculty.getType() != null) {
            return faculty.getType().getDisplayName();
        }
        return "N/A";
    }

    @Transactional
    public void initializeFaculties() {
        if (facultyRepository.count() == 0) {
            logger.info("No faculties found in the database. Initializing default faculties...");
            Arrays.stream(FacultyType.values()).forEach(type -> {
                Faculty faculty = new Faculty();
                faculty.setType(type);
                facultyRepository.save(faculty);
                logger.info("Created faculty: {}", type.getDisplayName());
            });
            logger.info("Default faculties initialization complete.");
        } else {
            logger.info("Faculties already exist in the database. Skipping initialization.");
        }
    }
}
