package org.codequistify.master.domain.player.domain;

public enum PlayerStatus {
    ACTIVE,
    LOCKED,
    DELETED;

    public static PlayerStatus fromLocked(boolean locked) {
        return locked ? LOCKED : ACTIVE;
    }

    public boolean locked() {
        return this != ACTIVE;
    }
}
