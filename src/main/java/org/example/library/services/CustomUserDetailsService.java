package org.example.library.services;

import org.example.library.models.LibraryUser ;
import org.example.library.models.MyUserDetails;
import org.example.library.repositories.LibraryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LibraryUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibraryUser libraryUser = userRepository.findByUsername(username);
        if (libraryUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new MyUserDetails(libraryUser);
    }

    public MyUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("Ошибка: аутентификация не найдена.");
            return null;
        }
        System.out.println("Аутентификация найдена. Текущий пользователь: " + authentication.getName());
        if (authentication.getPrincipal() instanceof MyUserDetails) {
            return (MyUserDetails) authentication.getPrincipal();
        }
        System.out.println("Не удалось получить MyUserDetails.");
        return null;
    }


}
