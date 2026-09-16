package com.sipersurat.backend.config;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import com.sipersurat.backend.entity.User;
import com.sipersurat.backend.enums.UserRole;
import com.sipersurat.backend.enums.UserStatus;
import com.sipersurat.backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
@Component
public class FirstAdminSetup implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(FirstAdminSetup.class);
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    public FirstAdminSetup(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            Environment environment,
            EntityManager entityManager,
            PlatformTransactionManager transactionManager
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.entityManager = entityManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }
    @Override
    public void run(ApplicationArguments args) {
        boolean enabled = Boolean.parseBoolean(
                environment.getProperty("SIPERSURAT_BOOTSTRAP_ENABLED", "false")
        );
        if (!enabled) {
            return;
        }
        String email = environment.getProperty("SIPERSURAT_BOOTSTRAP_EMAIL", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        String password = environment.getProperty("SIPERSURAT_BOOTSTRAP_PASSWORD", "");
        if (email.length() > 255 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalStateException("Setup dibatalkan: email tidak valid.");
        }
        if (password.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Setup dibatalkan: password minimal 12 karakter.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Setup dibatalkan: password maksimal 72 byte untuk BCrypt.");
        }
        Boolean created = transactionTemplate.execute(status -> {
            entityManager.createNativeQuery(
                    "LOCK TABLE users IN SHARE ROW EXCLUSIVE MODE"
            ).executeUpdate();
            if (repository.countByRole(UserRole.SUPER_ADMIN) > 0) {
                return false;
            }
            if (repository.existsByEmailIgnoreCase(email)) {
                throw new IllegalStateException(
                        "Setup dibatalkan: email sudah digunakan. Akun lama tidak diubah."
                );
            }
            User user = new User();
            user.setName("Super Administrator");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRole(UserRole.SUPER_ADMIN);
            user.setDepartment("Administrasi");
            user.setStatus(UserStatus.ACTIVE);
            repository.saveAndFlush(user);
            return true;
        });
        if (Boolean.TRUE.equals(created)) {
            log.info("SETUP BERHASIL: Super Admin pertama sudah tersimpan. Matikan mode setup.");
        } else {
            log.warn("SETUP DILEWATI: Super Admin sudah ada. Tidak ada akun atau password yang diubah.");
        }
    }
}