package org.codequistify.master.stage.domain;

public enum AnswerType {
    MULTIPLE_CHOICE("M"),
    SHORT_ANSWER("S"),
    PRACTICAL("P"),
    DESCRIPTION("D");

    private final String code;

    AnswerType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
