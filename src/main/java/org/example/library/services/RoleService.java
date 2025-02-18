package org.example.library.services;

import org.example.library.models.Role;
import org.example.library.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public void initializeRoles() {
        if (roleRepository.findByRoleName("STUDENT") == null) {
            roleRepository.save(new Role("STUDENT"));
        }
        if (roleRepository.findByRoleName("TEACHER") == null) {
            roleRepository.save(new Role("TEACHER"));
        }
        if (roleRepository.findByRoleName("LIBRARIAN") == null) {
            roleRepository.save(new Role("LIBRARIAN"));
        }
        if (roleRepository.findByRoleName("ADMIN") == null) {
            roleRepository.save(new Role("ADMIN"));
        }
    }
}
