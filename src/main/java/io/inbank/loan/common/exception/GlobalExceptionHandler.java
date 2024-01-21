package io.inbank.loan.common.exception;

import io.inbank.loan.common.model.error.InvalidOperationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

import static io.inbank.loan.common.exception.ValidationErrorType.ARGUMENT_NOT_VALID;
import static io.inbank.loan.common.exception.ValidationErrorType.CONSTRAINT_VIOLATION;


@Slf4j
@ControllerAdvice
@Order(0)
@ConditionalOnProperty(value = "inbank.controller-advice.enabled", havingValue = "true")
public class GlobalExceptionHandler {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }


    @ExceptionHandler(InvalidOperationException.class)
    @ResponseBody
    public InvalidOperationErrorResponse handleValidationException(
            HttpServletRequest request, HttpServletResponse response, InvalidOperationException e
    ) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.CONFLICT.value());

        return new InvalidOperationErrorResponse(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ValidationErrorResponse handleException(HttpServletResponse response, ConstraintViolationException e) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(CONSTRAINT_VIOLATION);
        errorResponse.getRows().addAll(
                e.getConstraintViolations()
                        .stream()
                        .map(error -> new ValidationErrorRow(
                                getFieldName(error),
                                getValidatorName(error),
                                parseMessage(error.getMessage())
                        ))
                        .collect(Collectors.toList())
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ValidationErrorResponse handleException(MethodArgumentNotValidException e, HttpServletResponse response) {
        return translateBindingResult(e.getBindingResult(), response, ARGUMENT_NOT_VALID);
    }

    private ValidationErrorResponse translateBindingResult(
            BindingResult bindingResult,
            HttpServletResponse response,
            ValidationErrorType type
    ) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(type);
        errorResponse.getRows().addAll(bindingResult.getFieldErrors().stream().map(error -> new ValidationErrorRow(
                error.getField(),
                error.getCodes() != null ? error.getCodes()[0].split("\\.")[0] : null,
                parseMessage(error.getDefaultMessage())
        )).collect(Collectors.toList()));

        errorResponse.getRows().addAll(bindingResult.getGlobalErrors().stream().map(error -> new ValidationErrorRow(
                error.getObjectName(),
                error.getCodes() != null ? error.getCodes()[0].split("\\.")[0] : null,
                parseMessage(error.getDefaultMessage())
        )).collect(Collectors.toList()));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return errorResponse;
    }

    private static String getFieldName(ConstraintViolation<?> error) {
        String[] parts = error.getPropertyPath().toString().split("\\.");

        return parts[parts.length - 1];
    }

    private static String getValidatorName(ConstraintViolation<?> constraintViolation) {
        String validatorName = constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        String[] parts = validatorName.split("\\.");

        return parts[parts.length - 1];
    }

    private static String parseMessage(String message) {
        if (message != null
                && (message.contains("java.util.Date") || message.contains("java.time.LocalDate"))
                && message.contains("java.lang.IllegalArgumentException")) {
            return message.substring(message.indexOf("java.lang.IllegalArgumentException") + 36);
        }

        return message;
    }
}
