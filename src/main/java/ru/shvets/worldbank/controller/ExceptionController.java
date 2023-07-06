package ru.shvets.worldbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.shvets.worldbank.dto.ErrorResponseDTO;
import ru.shvets.worldbank.util.DataIllegalArgumentException;
import ru.shvets.worldbank.util.DataNotFoundException;
import ru.shvets.worldbank.util.DataNullPointerException;
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

    @ExceptionHandler(DataIllegalArgumentException.class)
    private ResponseEntity<ErrorResponseDTO> handleException(DataIllegalArgumentException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataNullPointerException.class)
    private ResponseEntity<ErrorResponseDTO> handleException(DataNullPointerException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataNotFoundException.class)
    private ResponseEntity<ErrorResponseDTO> handleException(DataNotFoundException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }
}
