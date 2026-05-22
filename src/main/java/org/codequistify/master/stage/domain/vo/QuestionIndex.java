package org.codequistify.master.stage.domain.vo;

public record QuestionIndex(int value) {
    public static final QuestionIndex FIRST = new QuestionIndex(1);

    public QuestionIndex {
        if (value < 1) {
            throw new IllegalArgumentException("question index must be positive");
        }
    }

    public static QuestionIndex of(int value) {
        return new QuestionIndex(value);
    }

    public QuestionIndex next() {
        return new QuestionIndex(value + 1);
    }

    public boolean isFirst() {
        return value == 1;
    }
}
