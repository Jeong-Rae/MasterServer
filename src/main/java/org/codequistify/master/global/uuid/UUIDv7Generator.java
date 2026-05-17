package org.codequistify.master.global.uuid;

import com.github.f4b6a3.uuid.UuidCreator;

public final class UUIDv7Generator {
    private UUIDv7Generator() {}

    public static String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
