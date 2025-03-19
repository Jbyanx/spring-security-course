package com.bycorp.spring_security_course.exception;

import com.bycorp.spring_security_course.dto.response.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error("MethodArgumentNotValidException: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiError(
                        "Error, verifique los datos enviados",
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                )
        );
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        log.error("HttpRequestMethodNotSupportedException: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ApiError(
                        "Error, el metodo solicitado para este endpoint no esta soportado",
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        ex.getMessage()
                )
        );
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFoundException(ProductNotFoundException ex){
        log.error("ProductNotFoundException: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(
                        "Error, el producto solicitado no existe en BBDD",
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                )
        );
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFoundException(CategoryNotFoundException ex){
        log.error("CategoryNotFoundException: {} ", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(
                        "Error, la categoria solicitada no existe en BBDD",
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()
                )
        );
    }
}
