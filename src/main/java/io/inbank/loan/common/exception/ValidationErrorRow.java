package io.inbank.loan.common.exception;

import lombok.*;

import java.util.Comparator;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorRow implements Comparable<ValidationErrorRow> {

    private static final Comparator<String> NULL_SAFE_STRING_COMPARATOR = Comparator.nullsFirst(
            String::compareToIgnoreCase
    );

    private String field;

    private String reason;

    private String message;

    @Override
    public int compareTo(ValidationErrorRow o) {
        return Comparator.nullsFirst(
                Comparator.comparing(ValidationErrorRow::getField, NULL_SAFE_STRING_COMPARATOR)
                        .thenComparing(ValidationErrorRow::getReason, NULL_SAFE_STRING_COMPARATOR)
                        .thenComparing(ValidationErrorRow::getMessage, NULL_SAFE_STRING_COMPARATOR)
        ).compare(this, o);
    }
}
