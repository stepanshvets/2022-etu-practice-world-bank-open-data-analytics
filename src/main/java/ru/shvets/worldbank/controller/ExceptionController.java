package ru.shvets.worldbank.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
//    @ExceptionHandler(JwtAuthenticationException.class)
//    private ResponseEntity<ErrorResponseDTO> handleException(JwtAuthenticationException e) {
//        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
//        errorResponseDTO.setTimestamp(LocalDateTime.now());
//        errorResponseDTO.setMessage(e.getMessage());
//        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
//    }
}
