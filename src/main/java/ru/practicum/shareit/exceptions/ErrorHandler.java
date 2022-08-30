package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateException(final DuplicateException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final NotFoundException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyResultDataAccessException(final EmptyResultDataAccessException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        logMakeNote(e);
        ErrorResponse answer;
        try {
            answer = new ErrorResponse(String.format("При обработке поля %s произошла ошибка. %s",
                    Objects.requireNonNull(e.getFieldError()).getField(),
                    e.getFieldError().getDefaultMessage()));
        } catch (NullPointerException ex) {
            answer = new ErrorResponse("Ошибка валидации");
        }
        return answer;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleValidationOwnerException(final OwnerVerificationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        logMakeNote(e);
        return new ErrorResponse("Произошла ошибка. Попытка создать объект с уникальным элементом, " +
                "который уже существует в Базе Данных");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatus(final UnsupportedStatusException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }

    private void logMakeNote(Exception e) {
        log.warn("При обработке запроса произошла ошибка: '{}'", e.getMessage());
    }
}
