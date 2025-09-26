package com.gamestore.platform.repository;

import com.gamestore.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    List<User> findByIsBanned(boolean isBanned);

    default User findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // Метод поиска пользователей по имени или email (без учета регистра)
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findByUsernameOrEmale(@Param("query") String query);

}
/*
   USER:     testuser / password123
   ADMIN:    admin / admin123
   SUPERADMIN: superadmin / super123
* */