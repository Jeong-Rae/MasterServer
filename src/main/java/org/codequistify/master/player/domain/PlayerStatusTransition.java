package org.codequistify.master.domain.player.domain;

public enum PlayerStatusTransition {
    ACTIVATE {
        @Override
        PlayerStatus apply(PlayerStatus current) {
            if (current == PlayerStatus.DELETED) {
                throw new IllegalStateException("deleted player cannot be activated");
            }
            return PlayerStatus.ACTIVE;
        }
    },
    LOCK {
        @Override
        PlayerStatus apply(PlayerStatus current) {
            if (current == PlayerStatus.DELETED) {
                throw new IllegalStateException("deleted player cannot be locked");
            }
            return PlayerStatus.LOCKED;
        }
    },
    DELETE {
        @Override
        PlayerStatus apply(PlayerStatus current) {
            return PlayerStatus.DELETED;
        }
    };

    abstract PlayerStatus apply(PlayerStatus current);
}
