package org.codequistify.master.domain.player.domain.vo;

public record EncodedPassword(String value) {
    public EncodedPassword {
        if (value == null) {
            value = "";
        }
    }
}
