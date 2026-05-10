package org.codequistify.master.domain.player.domain.authorization;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum PlayerRoleType {
    PLAYER("ROLE_PLAYER"),
    ADMIN("ROLE_ADMIN"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN"),
    TEMPORARY("TEMPORARY"),
    GUEST("ROLE_GUEST");

    private final String role;

    PlayerRoleType(String role) {
        this.role = role;
    }

    public static Optional<PlayerRoleType> findByRole(String role) {
        return Arrays.stream(values())
                .filter(roleType -> roleType.role.equals(role))
                .findFirst();
    }
}
