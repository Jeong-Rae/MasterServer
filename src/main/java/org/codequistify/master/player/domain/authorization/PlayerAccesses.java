package org.codequistify.master.domain.player.domain.authorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record PlayerAccesses(Set<PlayerAccessType> values) {
    public PlayerAccesses {
        values = values == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }

    public static PlayerAccesses empty() {
        return new PlayerAccesses(Set.of());
    }

    public static PlayerAccesses basic() {
        return of(PlayerAccessType.BASIC_PROBLEMS_ACCESS);
    }

    public static PlayerAccesses of(PlayerAccessType... accesses) {
        if (accesses == null) {
            return empty();
        }
        return new PlayerAccesses(new LinkedHashSet<>(Arrays.asList(accesses)));
    }

    public static PlayerAccesses fromStoredAuthorities(List<String> authorities) {
        if (authorities == null) {
            return empty();
        }
        return new PlayerAccesses(authorities.stream()
                .map(PlayerAccessType::findByPermission)
                .flatMap(java.util.Optional::stream)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
    }

    public boolean contains(PlayerAccessType access) {
        return values.contains(access);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public java.util.stream.Stream<String> storedAuthorities() {
        return values.stream().map(PlayerAccessType::getPermission);
    }
}
