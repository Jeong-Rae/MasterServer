package org.codequistify.master.domain.player.security;

import java.util.Collection;
import java.util.stream.Stream;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccesses;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record PlayerPrincipal(PlayerId id, PlayerAuthorization authorization, boolean locked) implements UserDetails {
    public PlayerPrincipal {
        if (id == null) {
            throw new IllegalArgumentException("principal id must not be null");
        }
        authorization = authorization == null ? PlayerAuthorization.basicPlayer() : authorization;
    }

    public PlayerRoles roles() {
        return authorization.roles();
    }

    public PlayerAccesses accesses() {
        return authorization.accesses();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(roles().storedAuthorities(), accesses().storedAuthorities())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return id.value();
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
