package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Yahan role ko dynamic banaya ja sakta hai agar User entity me role ka field ho
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Email ko username treat kar rahe hain
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Future me expire check logic add kiya ja sakta hai
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Lock status check future me add kiya ja sakta hai
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password expiry check future me add kiya ja sakta hai
    }

    @Override
    public boolean isEnabled() {
        return true; // Agar account disable karne ka feature ho to yahan check kare
    }
}
