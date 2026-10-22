package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.domain.User;
import syksy26.swu_tracker.domain.UserRepository;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsernameShouldReturnUser() {
        User user = userRepository.findByUsername("user");
        assertThat(user).isNotNull();
        assertThat(user.getRole()).isEqualTo("USER");
    }

    @Test
    void createNewUser() {
        String uniqueUsername = "testuser" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(uniqueUsername);
        user.setPassword("$2a$10$hashedpassword");
        user.setEmail(uniqueUsername + "@test.fi");
        user.setRole("USER");
        userRepository.save(user);

        assertThat(user.getId()).isNotNull();
        assertThat(userRepository.findByUsername(uniqueUsername)).isNotNull();
    }

    @Test
    void deleteUser() {
        String uniqueUsername = "deleteuser" + System.currentTimeMillis();
        User user = new User();
        user.setUsername(uniqueUsername);
        user.setPassword("$2a$10$hashedpassword");
        user.setEmail(uniqueUsername + "@test.fi");
        user.setRole("USER");
        userRepository.save(user);
        Long id = user.getId();

        userRepository.deleteById(id);
        assertThat(userRepository.findById(id)).isEmpty();
    }
}