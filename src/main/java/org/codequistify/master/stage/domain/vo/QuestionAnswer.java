package org.codequistify.master.stage.domain.vo;

public record QuestionAnswer(String value) {
    public QuestionAnswer {
        if (value == null) {
            throw new IllegalArgumentException("question answer must not be null");
        }
    }

    public static QuestionAnswer of(String value) {
        return new QuestionAnswer(value);
    }

    public boolean matches(SubmittedAnswer submittedAnswer) {
        if (submittedAnswer == null || submittedAnswer.value() == null) {
            return false;
        }
        return value.equalsIgnoreCase(submittedAnswer.value());
    }
}
