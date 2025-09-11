package org.example.library.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.library.models.LibraryUser;
import org.example.library.models.MyUserDetails;
import org.example.library.repositories.LibraryUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final LibraryUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(LibraryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibraryUser libraryUser = userRepository.findByUsername(username);
        if (libraryUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new MyUserDetails(libraryUser);
    }

    @Transactional(readOnly = true)
    public MyUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.warn("Authentication object not found in SecurityContext.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof MyUserDetails) {
            return (MyUserDetails) principal;
        }

        logger.debug("Principal is not an instance of MyUserDetails. Principal type: {}", principal.getClass().getName());
        return null;
    }

    @Transactional(readOnly = true)
    public LibraryUser getUserById(Long userId) {
        // This now uses the optimized findById from the repository
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }
}
