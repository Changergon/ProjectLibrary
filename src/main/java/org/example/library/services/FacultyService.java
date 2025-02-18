package org.example.library.services;

import org.example.library.models.Faculty;
import org.example.library.models.FacultyType;
import org.example.library.repositories.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    // Метод для поиска факультета по идентификатору
    public Faculty findById(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    // Метод для сохранения нового факультета
    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    // Метод для получения всех факультетов
    public List<Faculty> findAll() {
        return facultyRepository.findAll();
    }

    // Метод для удаления факультета по идентификатору
    public void deleteById(Long id) {
        facultyRepository.deleteById(id);
    }

    // Метод для поиска факультетов по типу
    public List<Faculty> findByType(FacultyType type) {
        return facultyRepository.findByType(type);
    }

    public String getFacultyDisplayName(Faculty faculty) {
        if (faculty != null && faculty.getType() != null) {
            return faculty.getType().getDisplayName();
        }
        return null; // Или можно вернуть какое-то значение по умолчанию
    }

    // Метод для инициализации факультетов
    public void initializeFaculties() {
        // Проверяем, есть ли уже факультеты в базе данных
        if (facultyRepository.count() == 0) {
            Arrays.stream(FacultyType.values()).forEach(type -> {
                Faculty faculty = new Faculty();
                faculty.setType(type);
                facultyRepository.save(faculty);
            });
        }
    }

}
