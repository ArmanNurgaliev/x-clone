package ru.arman.backendxclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.arman.backendxclone.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
