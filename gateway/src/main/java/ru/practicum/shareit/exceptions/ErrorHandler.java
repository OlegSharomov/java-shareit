package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<Object> handleExceptionForDeleteMethod(final ExceptionForDeleteMethod e) {
//        log.warn("Случилось непредвиденное: Класс ошибки: '{}', \n Сообщение ошибки: '{}', " +
//                "\n СтекТрейс: '{}'",e.getClass(), e.getMessage(), e.getStackTrace());
//        logMakeNote(e);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ErrorResponse handleDuplicateException(final DuplicateException e) {
//        logMakeNote(e);
//        return new ErrorResponse(String.format("DuplicateException. %s", e.getMessage()));
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleItemNotFoundException(final NotFoundException e) {
//        logMakeNote(e);
//        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
//    }
//
//
////    @ExceptionHandler
////    @ResponseStatus(HttpStatus.NOT_FOUND)
////    public ErrorResponse handleEmptyResultDataAccessException(final EmptyResultDataAccessException e) {
////        logMakeNote(e);
////        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
////    }
//
    // Обрабатывает аннотации @NotNull, @Size, @Email
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        logMakeNote(e);
        ErrorResponse answer;
        try {
            answer = new ErrorResponse(String.format("MethodArgumentNotValidException. При обработке поля %s произошла ошибка. %s",
                    Objects.requireNonNull(e.getFieldError()).getField(),
                    e.getFieldError().getDefaultMessage()));
        } catch (NullPointerException ex) {
            answer = new ErrorResponse("Ошибка валидации");
        }
        return answer;
    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ErrorResponse handleValidationOwnerException(final OwnerVerificationException e) {
//        logMakeNote(e);
//        return new ErrorResponse(String.format("Произошла ошибка. %s", e.getMessage()));
//    }
//
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("ValidationException.  %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("MissingRequestHeaderException.  %s", e.getMessage()));
    }
//
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final javax.validation.ConstraintViolationException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("Произошла ошибка. %s",
                e.getConstraintViolations().iterator().next().getMessage()));
    }
//
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        logMakeNote(e);
        return new ErrorResponse(String.format("MissingServletRequestParameterException.  %s", e.getMessage()));
    }
//
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        logMakeNote(e);
        return new ErrorResponse(e.getMessage());
    }
//
    private void logMakeNote(Exception e) {
        log.warn("При обработке запроса произошла ошибка: '{}', '{}'", e.getMessage(), e.getStackTrace());
    }

//        @ExceptionHandler
//    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
//    public ErrorResponse handleAllExceptions(final Exception e) {
//        log.warn("Случилось непредвиденное: Класс ошибки: '{}', \n Сообщение ошибки: '{}', " +
//                "\n СтекТрейс: '{}'",e.getClass(), e.getMessage(), e.getStackTrace());
//        logMakeNote(e);
//        return new ErrorResponse(e.getMessage());
//    }
}
