package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.DTO.UserRoleUpdateDTO;
import org.example.library.models.Faculty;
import org.example.library.models.LibraryUser;
import org.example.library.models.Role;
import org.example.library.repositories.FacultyRepository;
import org.example.library.repositories.LibraryUserRepository;
import org.example.library.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final LibraryUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(LibraryUserRepository userRepository, RoleRepository roleRepository, FacultyRepository facultyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<LibraryUser> getAllUsers() {
        logger.info("Fetching all users with roles and faculties.");
        return userRepository.findAll();
    }

    @Transactional
    public LibraryUser createUser(LibraryUser user) {
        logger.info("Creating a new user with username: {}", user.getUsername());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserRoles(List<UserRoleUpdateDTO> updates) {
        logger.info("Starting batch update for user roles and faculties.");
        for (UserRoleUpdateDTO update : updates) {
            LibraryUser user = userRepository.findById(update.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + update.getUserId()));

            if (update.getRoles() != null) {
                List<Role> roles = roleRepository.findAllByRoleNameIn(update.getRoles());
                user.getRoles().clear();
                user.getRoles().addAll(roles);
            }

            if (update.getFacultyIds() != null) {
                List<Faculty> faculties = facultyRepository.findAllById(update.getFacultyIds());
                user.getFaculties().clear();
                user.getFaculties().addAll(faculties);
            }

            logger.info("Updated roles and faculties for user: {}", user.getUsername());
        }
        logger.info("Batch update complete.");
    }

    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + roleId));

        user.getRoles().add(role);
        logger.info("Assigned role '{}' to user '{}'", role.getRoleName(), user.getUsername());
    }

    @Transactional
    public void assignFacultyToUser(Long userId, Long facultyId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with ID: " + facultyId));

        user.getFaculties().add(faculty);
        logger.info("Assigned faculty '{}' to user '{}'", faculty.getType(), user.getUsername());
    }

    @Transactional
    public void removeFacultyFromUser(Long userId, Long facultyId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with ID: " + facultyId));

        user.getFaculties().remove(faculty);
        logger.info("Removed faculty '{}' from user '{}'", faculty.getType(), user.getUsername());
    }
}
