package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        logMakeNote(e);
        return new ErrorResponse(
                String.format("При обработке поля %s %s", Objects.requireNonNull(e.getFieldError()).getField(),
                        e.getFieldError().getDefaultMessage()));
    }


    private void logMakeNote(Exception e) {
        log.warn("При обработке запроса произошла ошибка: '{}'", e.getMessage());
    }
}
