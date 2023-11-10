package ru.arman.backendxclone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.arman.backendxclone.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByNameOrEmailOrLogin(String name, String email, String login);

    @Query(value = "SELECT DISTINCT u FROM User u where u.name LIKE %:query% or u.email LIKE %:query%")
    Page<User> findByQuery(Pageable pageable, String query);

    Optional<User> findByLogin(String login);
}