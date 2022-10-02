package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final NotFoundException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleValidationOwnerException(final OwnerVerificationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final org.hibernate.exception.ConstraintViolationException e) {
        logMakeNote(e);
        return new ErrorResponse("An error has occurred. Attempt to create an object with a unique element " +
                "that already exists in the Database");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final javax.validation.ConstraintViolationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Error: %s",
                e.getConstraintViolations().iterator().next().getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }

    private void logMakeNote(Exception e) {
        log.warn("An error occurred while processing the request: '{}', '{}'", e.getMessage(), e.getStackTrace());
    }
}
