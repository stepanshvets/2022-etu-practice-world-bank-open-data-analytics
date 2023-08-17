package ru.shvets.worldbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shvets.worldbank.model.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    void deleteByUserId(Integer userId);
}
