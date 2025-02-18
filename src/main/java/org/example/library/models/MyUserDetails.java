package org.example.library.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {

    private final LibraryUser libraryUser ;

    public MyUserDetails(LibraryUser libraryUser) {
        this.libraryUser  = libraryUser ;
    }

    public Long getId() {
        return libraryUser.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return libraryUser .getRoles().stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getRoleName())
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return libraryUser .getPasswordHash();
    }

    @Override
    public String getUsername() {
        return libraryUser .getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Логика по умолчанию
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Логика по умолчанию
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Логика по умолчанию
    }

    @Override
    public boolean isEnabled() {
        return true; // Логика по умолчанию
    }
}
