package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.shvets.worldbank.dto.AuthRequestDTO;
import ru.shvets.worldbank.dto.AuthResponseDTO;
import ru.shvets.worldbank.dto.ErrorResponseDTO;
import ru.shvets.worldbank.dto.RegistrationRequestDTO;
import ru.shvets.worldbank.service.AuthService;
import ru.shvets.worldbank.util.AuthValidationException;
import ru.shvets.worldbank.util.JwtAuthenticationException;
import ru.shvets.worldbank.util.RegistrationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO requestDTO)
            throws AuthenticationException {
        // todo: fully validate keys and values of JSON in service layer
        return ResponseEntity.ok(authService.authenticate(requestDTO));
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponseDTO> registration(@RequestBody RegistrationRequestDTO requestDTO)
            throws RegistrationException {
        // todo: fully validate keys and values of JSON in service layer
        return ResponseEntity.ok(authService.register(requestDTO));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response)
            throws RegistrationException {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam(required = false, defaultValue = "false") boolean fromAllDevices) {
        if (fromAllDevices)
            authService.logoutCompletely(request, response);
        else
            authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(LockedException.class)
    private ResponseEntity<ErrorResponseDTO> handleException(LockedException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponseDTO> handleException(AuthenticationException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage("Incorrect email or password");
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponseDTO> handleException(RegistrationException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponseDTO> handleException(AuthValidationException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponseDTO> handleException(JwtAuthenticationException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }
}
