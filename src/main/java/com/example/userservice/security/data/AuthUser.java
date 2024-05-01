package com.example.userservice.security.data;

import com.example.userservice.entity.Role;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class AuthUser implements UserDetails {
    @Getter
    @Id
    private String id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    private Collection<? extends GrantedAuthority> roles;

    public AuthUser(String id, String username, String password, Collection<Role> roleList) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roleList
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
