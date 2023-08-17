package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.shvets.worldbank.dto.AuthRequestDTO;
import ru.shvets.worldbank.dto.ErrorResponseDTO;
import ru.shvets.worldbank.service.AdminService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/promote")
    public void promote(@RequestBody AuthRequestDTO authRequestDTO) {
        adminService.promote(authRequestDTO);
    }

    @PostMapping("/downgrade")
    public void downgrade(@RequestBody AuthRequestDTO authRequestDTO) {
        adminService.downgrade(authRequestDTO);
    }

    @PostMapping("/lock")
    public void lock(@RequestBody AuthRequestDTO authRequestDTO) {
        adminService.lock(authRequestDTO);
    }

    @PostMapping("/unlock")
    public void unlock(@RequestBody AuthRequestDTO authRequestDTO) {
        adminService.unlock(authRequestDTO);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponseDTO> handleException(AuthenticationException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setTimestamp(LocalDateTime.now());
        errorResponseDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }
}
