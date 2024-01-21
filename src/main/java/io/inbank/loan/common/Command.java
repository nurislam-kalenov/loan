package io.inbank.loan.common;

import io.inbank.loan.common.exception.BusinessException;

public interface Command<T, R> {
    R execute(T parameters) throws BusinessException;
}
