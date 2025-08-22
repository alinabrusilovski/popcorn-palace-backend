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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
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
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input", "Validation failed", req);
        pd.setProperty("errors", errors);
        pd.setProperty("code", "INVALID_INPUT");
        return pd;
    }

    /* ------------ 400: Validation (path/query) ------------ */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input", "Validation failed", req);
        pd.setProperty("errors", errors);
        pd.setProperty("code", "INVALID_INPUT");
        return pd;
    }

    /* ------------ 400: Binding/Type/Missing ------------ */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onBindException(BindException ex, HttpServletRequest req) {
        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Bad request",
                "https://errors.popcornpalace.dev/bad-request", "Invalid parameters", req);
        pd.setProperty("errors", errors);
        pd.setProperty("code", "BAD_REQUEST");
        return pd;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Bad request",
                "https://errors.popcornpalace.dev/bad-request",
                "Missing required parameter: " + ex.getParameterName(), req);
        pd.setProperty("code", "BAD_REQUEST");
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String detail = "Parameter '%s' has invalid value '%s'".formatted(
                ex.getName(), ex.getValue());
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Bad request",
                "https://errors.popcornpalace.dev/bad-request", detail, req);
        pd.setProperty("code", "BAD_REQUEST");
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Bad request",
                "https://errors.popcornpalace.dev/bad-request", "Malformed JSON", req);
        pd.setProperty("code", "BAD_REQUEST");
        return pd;
    }

    /* ------------ 404 ------------ */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail onNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "Resource not found",
                "https://errors.popcornpalace.dev/not-found", ex.getMessage(), req);
        pd.setProperty("code", "NOT_FOUND");
        return pd;
    }

    /* ------------ 409 ------------ */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail onDomainConflict(ConflictException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.CONFLICT, "Conflict",
                "https://errors.popcornpalace.dev/conflict", ex.getMessage(), req);
        pd.setProperty("code", "CONFLICT");
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail onIntegrityConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.CONFLICT, "Integrity violation",
                "https://errors.popcornpalace.dev/conflict", "Duplicate or integrity constraint violation", req);
        pd.setProperty("code", "CONFLICT");
        return pd;
    }

    /* ------------ 400 generic ------------ */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Invalid input",
                "https://errors.popcornpalace.dev/invalid-input", ex.getMessage(), req);
        pd.setProperty("code", "INVALID_INPUT");
        return pd;
    }

    /* ------------ 500 ------------ */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail onGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {} {}", req.getMethod(), req.getRequestURI(), ex);
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error",
                "https://errors.popcornpalace.dev/server-error", "Something went wrong", req);
        pd.setProperty("code", "SERVER_ERROR");
        return pd;
    }

    /* ------------ helper ------------ */
    private ProblemDetail problem(HttpStatus status, String title, String type, String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setType(URI.create(type));
        pd.setDetail(detail);
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        return pd;
    }
}
