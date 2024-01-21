package io.inbank.loan.common.model.error;

import io.inbank.loan.common.exception.EntityNotFoundException;
import lombok.Getter;

@Getter
public class EntityNotFoundErrorResponse {

    private final String entity;

    private final String field;

    private final String value;

    private final String message;

    public EntityNotFoundErrorResponse(EntityNotFoundException e) {
        entity = e.getEntity();
        field = e.getField();
        value = e.getValue();
        message = e.getMessage();
    }
}
