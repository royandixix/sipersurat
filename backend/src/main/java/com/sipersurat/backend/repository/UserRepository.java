package com.sipersurat.backend.repository;
import java.util.Optional;
import com.sipersurat.backend.entity.User;
import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    long countByStatus(UserStatus status);
    long countByRole(UserRole role);
    long countByRoleAndStatus(UserRole role, UserStatus status);
    @Query("""
            SELECT u
            FROM User u
            WHERE (
                :search IS NULL OR
                :search = '' OR
                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.department) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (:role IS NULL OR u.role = :role)
            AND (:status IS NULL OR u.status = :status)
            """)
    Page<User> search(
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("status") UserStatus status,
            Pageable pageable
    );
}