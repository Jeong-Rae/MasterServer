package org.codequistify.master.domain.player.domain.authentication;

public record OAuthIdentity(OAuthType type, String id) {
    public OAuthIdentity {
        if (type == null) {
            type = OAuthType.POL;
        }
        if (id == null) {
            id = "";
        }
    }

    public static OAuthIdentity local() {
        return new OAuthIdentity(OAuthType.POL, "0");
    }

    public static OAuthIdentity of(OAuthType type, String id) {
        return new OAuthIdentity(type, id);
    }
}
