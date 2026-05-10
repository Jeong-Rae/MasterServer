package org.codequistify.master.domain.player.domain.authorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record PlayerRoles(Set<PlayerRoleType> values) {
    public PlayerRoles {
        values = values == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }

    public static PlayerRoles empty() {
        return new PlayerRoles(Set.of());
    }

    public static PlayerRoles basic() {
        return of(PlayerRoleType.PLAYER);
    }

    public static PlayerRoles of(PlayerRoleType... roles) {
        if (roles == null) {
            return empty();
        }
        return new PlayerRoles(new LinkedHashSet<>(Arrays.asList(roles)));
    }

    public static PlayerRoles fromStoredAuthorities(List<String> authorities) {
        if (authorities == null) {
            return empty();
        }
        return new PlayerRoles(authorities.stream()
                .map(PlayerRoleType::findByRole)
                .flatMap(java.util.Optional::stream)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
    }

    public boolean contains(PlayerRoleType role) {
        return values.contains(role);
    }

    public boolean containsAll(PlayerRoles roles) {
        return values.containsAll(roles.values());
    }

    public boolean containsAny(PlayerRoles roles) {
        return roles.values().stream().anyMatch(values::contains);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public java.util.stream.Stream<String> storedAuthorities() {
        return values.stream().map(PlayerRoleType::getRole);
    }
}
