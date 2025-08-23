package com.popcornpalace.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ------------ 400: Validation (body) ------------ */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(ge -> errors.put(ge.getObjectName(), ge.getDefaultMessage()));
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input",
                "Validation failed",
                req,
                "INVALID_INPUT",
                Map.of("errors", errors)
        );
    }

    /* ------------ 400: Validation (path/query) ------------ */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input",
                "Validation failed",
                req,
                "INVALID_INPUT",
                Map.of("errors", errors)
        );
    }

    /* ------------ 400: Binding/Type/Missing ------------ */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onBindException(BindException ex, HttpServletRequest req) {
        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return problem(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                "https://errors.popcornpalace.dev/bad-request",
                "Invalid parameters",
                req,
                "BAD_REQUEST",
                Map.of("errors", errors)
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                "https://errors.popcornpalace.dev/bad-request",
                "Missing required parameter: " + ex.getParameterName(),
                req,
                "BAD_REQUEST"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String detail = "Parameter '%s' has invalid value '%s'".formatted(ex.getName(), ex.getValue());
        return problem(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                "https://errors.popcornpalace.dev/bad-request",
                detail,
                req,
                "BAD_REQUEST"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                "https://errors.popcornpalace.dev/bad-request",
                "Malformed JSON",
                req,
                "BAD_REQUEST"
        );
    }

    /* ------------ 404 ------------ */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail onNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "https://errors.popcornpalace.dev/not-found",
                ex.getMessage(),
                req,
                "NOT_FOUND"
        );
    }

    /* ------------ 405 ------------ */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ProblemDetail onMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method Not Allowed",
                "https://errors.popcornpalace.dev/method-not-allowed",
                "HTTP method is not supported for this endpoint",
                req,
                "METHOD_NOT_ALLOWED",
                Map.of("allowed", ex.getSupportedHttpMethods())
        );
    }

    /* ------------ 409 ------------ */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail onDomainConflict(ConflictException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.CONFLICT,
                "Conflict",
                "https://errors.popcornpalace.dev/conflict",
                ex.getMessage(),
                req,
                "CONFLICT"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail onIntegrityConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.CONFLICT,
                "Integrity violation",
                "https://errors.popcornpalace.dev/conflict",
                "Duplicate or integrity constraint violation",
                req,
                "CONFLICT"
        );
    }

    /* ------------ 400 generic ------------ */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input",
                ex.getMessage(),
                req,
                "INVALID_INPUT"
        );
    }

    /* ------------ 500 ------------ */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail onGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {} {}", req.getMethod(), req.getRequestURI(), ex);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                "https://errors.popcornpalace.dev/server-error",
                "Something went wrong",
                req,
                "SERVER_ERROR"
        );
    }

    /* ------------ helper ------------ */
    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String type,
            String detail,
            HttpServletRequest req,
            String code
    ) {
        return problem(status, title, type, detail, req, code, null);
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String type,
            String detail,
            HttpServletRequest req,
            String code,
            Map<String, ?> extraProps
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setType(URI.create(type));
        pd.setDetail(detail);

        // RFC 9457: instance — это идентификатор ресурса/запроса (путь)
        pd.setInstance(URI.create(req.getRequestURI()));

        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        if (code != null) {
            pd.setProperty("code", code);
        }
        if (extraProps != null && !extraProps.isEmpty()) {
            extraProps.forEach(pd::setProperty);
        }
        return pd;
    }
}
