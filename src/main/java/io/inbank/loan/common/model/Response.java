package io.inbank.loan.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Response<T> {

    private final T data;
}
