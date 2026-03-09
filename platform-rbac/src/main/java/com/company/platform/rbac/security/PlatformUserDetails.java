package com.company.platform.rbac.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class PlatformUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final Set<String> permissions;
    private final boolean enabled;

    public PlatformUserDetails(Long userId, String username, String password, Set<String> permissions, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.permissions = permissions;
        this.enabled = enabled;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return enabled;
    }
}
