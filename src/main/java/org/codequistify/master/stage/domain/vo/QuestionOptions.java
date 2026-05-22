package org.codequistify.master.stage.domain.vo;

import java.util.List;

public record QuestionOptions(List<Option> values) {
    public QuestionOptions {
        values = values == null ? List.of() : List.copyOf(values);
    }

    public static QuestionOptions of(List<String> values) {
        if (values == null) {
            return empty();
        }

        return new QuestionOptions(values.stream().map(Option::of).toList());
    }

    public static QuestionOptions empty() {
        return new QuestionOptions(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<String> rawValues() {
        return values.stream().map(Option::value).toList();
    }
}
