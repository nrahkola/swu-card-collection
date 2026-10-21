package syksy26.swu_tracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import syksy26.swu_tracker.domain.User;
import syksy26.swu_tracker.domain.UserRepository;

@SpringBootApplication
public class SwuTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwuTrackerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            if (userRepository.findByUsername("user") == null) {
                userRepository.save(new User("user", passwordEncoder.encode("user123"), "user@swu.com", "USER"));
            }
            if (userRepository.findByUsername("admin") == null) {
                userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "admin@swu.com", "ADMIN"));
            }
        };
    }
}