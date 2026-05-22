package org.codequistify.master.stage.domain.vo;

public record SubmittedAnswer(String value) {
    public static SubmittedAnswer of(String value) {
        return new SubmittedAnswer(value);
    }
}
