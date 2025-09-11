package org.example.library.services;

import org.example.library.models.Role;
import org.example.library.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void initializeRoles() {
        logger.info("Checking and initializing roles...");
        List<String> rolesToCreate = List.of("STUDENT", "TEACHER", "LIBRARIAN", "ADMIN");
        for (String roleName : rolesToCreate) {
            if (roleRepository.findByRoleName(roleName) == null) {
                roleRepository.save(new Role(roleName));
                logger.info("Created role: {}", roleName);
            }
        }
        logger.info("Role initialization complete.");
    }
}
