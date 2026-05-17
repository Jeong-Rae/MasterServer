package org.codequistify.master.domain.player.domain.authorization;

import java.util.List;

public record PlayerAuthorization(PlayerRoles roles, PlayerAccesses accesses) {
    public PlayerAuthorization {
        roles = roles == null ? PlayerRoles.empty() : roles;
        accesses = accesses == null ? PlayerAccesses.empty() : accesses;
    }

    public static PlayerAuthorization basicPlayer() {
        return new PlayerAuthorization(PlayerRoles.basic(), PlayerAccesses.basic());
    }

    public static PlayerAuthorization fromStoredAuthorities(List<String> authorities) {
        PlayerRoles roles = PlayerRoles.fromStoredAuthorities(authorities);
        PlayerAccesses accesses = PlayerAccesses.fromStoredAuthorities(authorities);
        if (roles.isEmpty() && accesses.isEmpty()) {
            return basicPlayer();
        }
        return new PlayerAuthorization(roles, accesses);
    }

    public List<String> toStoredAuthorities() {
        return java.util.stream.Stream.concat(roles.storedAuthorities(), accesses.storedAuthorities())
                .distinct()
                .toList();
    }

    public boolean hasRole(PlayerRoleType role) {
        return roles.contains(role);
    }

    public boolean hasAccess(PlayerAccessType access) {
        return accesses.contains(access);
    }
}
