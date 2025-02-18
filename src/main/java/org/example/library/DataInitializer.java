package org.example.library;

import org.example.library.services.FacultyService;
import org.example.library.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private FacultyService facultyService;

    @Override
    public void run(String... args) throws Exception {
        // Инициализация ролей
        roleService.initializeRoles();

        // Инициализация факультетов
        facultyService.initializeFaculties();
    }
}

