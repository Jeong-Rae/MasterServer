package org.codequistify.master.stage.domain.vo;

public record Option(String value) {
    public Option {
        if (value == null) {
            throw new IllegalArgumentException("option value must not be null");
        }
    }

    public static Option of(String value) {
        return new Option(value);
    }
}
