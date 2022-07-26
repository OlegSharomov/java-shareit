package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        logMakeNote(e);
        ErrorResponse answer;
        try {
            answer = new ErrorResponse(String.format("An error occurred while processing the %s field. %s",
                    Objects.requireNonNull(e.getFieldError()).getField(),
                    e.getFieldError().getDefaultMessage()));
        } catch (NullPointerException ex) {
            answer = new ErrorResponse("Validation error");
        }
        return answer;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Error: %s", e.getMessage()));
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
        return new ErrorResponse(String.format("Error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }

    private void logMakeNote(Exception e) {
        log.warn("Error: '{}', '{}'", e.getMessage(), e.getStackTrace());
    }

}
