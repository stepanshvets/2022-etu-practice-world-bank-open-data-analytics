package ru.shvets.worldbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shvets.worldbank.dto.ErrorResponseDTO;
import ru.shvets.worldbank.util.QueryParameterException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(QueryParameterException.class)
    private ResponseEntity<ErrorResponseDTO> handleException(QueryParameterException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
