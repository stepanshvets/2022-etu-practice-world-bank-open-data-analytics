package ru.shvets.worldbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.AuthRequestDTO;
import ru.shvets.worldbank.dto.AuthResponseDTO;
import ru.shvets.worldbank.dto.RegistrationRequestDTO;
import ru.shvets.worldbank.model.Role;
import ru.shvets.worldbank.model.Token;
import ru.shvets.worldbank.model.User;
import ru.shvets.worldbank.repository.TokenRepository;
import ru.shvets.worldbank.repository.UserRepository;
import ru.shvets.worldbank.security.JwtUtil;
import ru.shvets.worldbank.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthRequestDTOValidator authRequestDTOValidator;
    private final RegistrationRequestDTOValidator registrationRequestDTOValidator;

    @Autowired
    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, TokenRepository tokenRepository,
                       AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                       AuthRequestDTOValidator authRequestDTOValidator,
                       RegistrationRequestDTOValidator registrationRequestDTOValidator) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authRequestDTOValidator = authRequestDTOValidator;
        this.registrationRequestDTOValidator = registrationRequestDTOValidator;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO requestDTO)
            throws AuthenticationException, AuthValidationException {
        authRequestDTOValidator.validate(requestDTO);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword()));

        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        saveToken(user.getId(), refreshToken);
        return convertToAuthRequestDTO(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponseDTO register(RegistrationRequestDTO requestDTO)
            throws RegistrationException, AuthValidationException {
        registrationRequestDTOValidator.validate(requestDTO);
        User user = convertToUser(requestDTO);
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new RegistrationException("User with email " + user.getEmail() + " already exists");
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), requestDTO.getPassword()));

        user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        saveToken(user.getId(), refreshToken);
        return convertToAuthRequestDTO(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponseDTO refresh(HttpServletRequest request) throws JwtAuthenticationException {
        final String refreshToken = jwtUtil.getToken(request);
        String username = jwtUtil.extractUsername(refreshToken, true);
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User is not found!"));
        Token token = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new JwtAuthenticationException("JWT token is expired or invalid"));

        String accessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        updateToken(token, newRefreshToken);
        return convertToAuthRequestDTO(user, accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getToken(request);
        tokenRepository.deleteByRefreshToken(refreshToken);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    @Transactional
    public void logoutCompletely(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getToken(request);
        Token token = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new JwtAuthenticationException("JWT token is expired or invalid"));
        tokenRepository.deleteByUserId(token.getUserId());
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

    private void saveToken(Integer userId, String refreshToken) throws AuthenticationException {
        Token token = new Token();
        token.setUserId(userId);
        token.setRefreshToken(refreshToken);
        tokenRepository.save(token);
    }

    private void updateToken(Token token, String newRefreshToken) throws AuthenticationException {
        token.setRefreshToken(newRefreshToken);
        tokenRepository.save(token);
    }

    private User convertToUser(AuthRequestDTO requestDTO) {
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword());
        return user;
    }

    private User convertToUser(RegistrationRequestDTO requestDTO) {
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setPassword(requestDTO.getPassword());
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        return user;
    }

    private AuthResponseDTO convertToAuthRequestDTO(User user, String accessToken, String refreshToken) {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setEmail(user.getEmail());
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        return authResponseDTO;
    }
}
