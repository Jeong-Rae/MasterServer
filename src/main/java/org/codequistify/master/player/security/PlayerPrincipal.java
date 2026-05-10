package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.authorization.PlayerAccesses;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Stream;

public record PlayerPrincipal(
        String id,
        String email,
        String password,
        PlayerRoles roles,
        PlayerAccesses accesses,
        boolean locked
) implements UserDetails {
    public PlayerPrincipal {
        roles = roles == null ? PlayerRoles.empty() : roles;
        accesses = accesses == null ? PlayerAccesses.empty() : accesses;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(roles.storedAuthorities(), accesses.storedAuthorities())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !locked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !locked;
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }
}
