package ru.shvets.worldbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.AuthRequestDTO;
import ru.shvets.worldbank.model.Role;
import ru.shvets.worldbank.model.User;
import ru.shvets.worldbank.repository.UserRepository;

@Service
@Transactional
public class AdminService {
    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void promote(AuthRequestDTO authRequestDTO) {
        User user = userRepository.findByEmail(authRequestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found with email " + authRequestDTO.getEmail()));
        if (user.getRole() == Role.USER)
            user.setRole(Role.MANAGER);
    }

    public void downgrade(AuthRequestDTO authRequestDTO) {
        User user = userRepository.findByEmail(authRequestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found with email " + authRequestDTO.getEmail()));
        if (user.getRole() == Role.MANAGER)
            user.setRole(Role.USER);
    }

    public void lock(AuthRequestDTO authRequestDTO) {
        User user = userRepository.findByEmail(authRequestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found with email " + authRequestDTO.getEmail()));
        if (user.getLocked() == null || !user.getLocked())
            user.setLocked(Boolean.TRUE);
    }

    public void unlock(AuthRequestDTO authRequestDTO) {
        User user = userRepository.findByEmail(authRequestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found with email " + authRequestDTO.getEmail()));
        if (user.getLocked())
            user.setLocked(Boolean.FALSE);
    }
}
