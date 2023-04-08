package ru.shvets.worldbank.util;

import org.springframework.stereotype.Component;
import ru.shvets.worldbank.dto.AuthRequestDTO;

@Component
public class AuthRequestDTOValidator {
    public void validate(AuthRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()
                || requestDTO.getEmail().length() > 255 || requestDTO.getPassword() == null
                || requestDTO.getPassword().isEmpty() || requestDTO.getPassword().length() > 255)
            throw new ValidationException("Some fields are null or incorrect");
    }
}
