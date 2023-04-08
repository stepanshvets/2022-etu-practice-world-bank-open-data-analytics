package ru.shvets.worldbank.util;

import org.springframework.stereotype.Component;
import ru.shvets.worldbank.dto.RegistrationRequestDTO;

@Component
public class RegistrationRequestDTOValidator {
    public void validate(RegistrationRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()
                || requestDTO.getEmail().length() > 255 || requestDTO.getPassword() == null
                || requestDTO.getPassword().isEmpty() || requestDTO.getPassword().length() > 255
                || requestDTO.getFirstName() == null || requestDTO.getFirstName().isEmpty()
                || requestDTO.getFirstName().length() > 100 || requestDTO.getLastName() == null
                || requestDTO.getLastName().isEmpty() || requestDTO.getLastName().length() > 100)
            throw new ValidationException("Some fields are null or incorrect");
    }
}
