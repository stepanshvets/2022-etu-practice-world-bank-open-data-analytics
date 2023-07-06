package ru.shvets.worldbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shvets.worldbank.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
