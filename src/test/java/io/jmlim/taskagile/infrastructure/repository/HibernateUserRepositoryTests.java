package io.jmlim.taskagile.infrastructure.repository;

import io.jmlim.taskagile.domain.model.user.User;
import io.jmlim.taskagile.domain.model.user.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class HibernateUserRepositoryTests {

    @TestConfiguration
    public static class UserRepositoryTestConfiguration {
        @Bean
        public UserRepository userRepository(EntityManager entityManager) {
            return new HibernateUserRepository(entityManager);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 네임이 누락된 객체가 넘어왔을 경우 Fail")
    void save_nullUsernameUser_shouldFail() {
        User invalidUser = User.create(null, "hackerljm@gmail.com", "MyPassword!");
        assertThrows(PersistenceException.class, () -> userRepository.save(invalidUser));
    }

    @Test
    void save_nullEmailAddressUser_shouldFail() {
        User invalidUser = User.create("림정묵", null, "MyPassword!");
        assertThrows(PersistenceException.class, () -> userRepository.save(invalidUser));
    }

    @Test
    void save_nullPasswordUser_shouldFail() {
        User invalidUser = User.create("림정묵", "hackerljm@gmail.com", null);
        assertThrows(PersistenceException.class, () -> userRepository.save(invalidUser));
    }

    @Test
    void save_validUser_shouldSuccess() {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        User newUser = User.create(username, emailAddress, "MyPassword!");

        userRepository.save(newUser);
        /**
         * assertAll 로 각각의 테스트를 한번에 실행 할 경우 그 안에 실패한 케이스가 여러개가 있을 경우 한번에 볼 수 있다.
         * assertAll 사용하지 않을 시 중간에 테스트가 실패날 경우 그 이후에 실패한 케이스가 있다해도 확인할 수 없었다.
         */

        assertAll(
                () -> assertNotNull(newUser.getId()),
                () -> assertNotNull(newUser.getCreatedDate()),

                () -> assertEquals(username, newUser.getUsername()),
                () -> assertEquals(emailAddress, newUser.getEmailAddress()),
                () -> assertEquals("", newUser.getFirstName()),
                () -> assertEquals("", newUser.getLastName())
        );

    }

    @Test
    void save_usernameAlreadyExist_shouldFail() {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";

        User alreadyExist = User.create(username, emailAddress, password);
        userRepository.save(alreadyExist);

        User newUser = User.create(username, "new@gmail.com", "1123");
        PersistenceException persistenceException = assertThrows(PersistenceException.class, () -> userRepository.save(newUser));
        String cause = persistenceException.getCause().getClass().toString();
        System.out.println(cause);

        assertEquals(ConstraintViolationException.class.toString(), cause);
    }

    @Test
    void save_emailAddressAlreadyExist_shouldFail() {
        String username = "림정묵";
        String emailAddress = "hackerljm@gmail.com";
        String password = "MyPassword!";

        User alreadyExist = User.create(username, emailAddress, password);
        userRepository.save(alreadyExist);

        User newUser = User.create("new", emailAddress, "1123");
        PersistenceException persistenceException = assertThrows(PersistenceException.class, () -> userRepository.save(newUser));
        String cause = persistenceException.getCause().getClass().toString();
        System.out.println(cause);

        assertEquals(ConstraintViolationException.class.toString(), cause);
    }

    @Test
    void findByEmailAddress_notExist_shouldReturnEmptyResult() {
        String emailAddress = "hackerljm@gmail.com";
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElse(null);

        assertNull(user);
    }

    @Test
    void findByEmailAddress_exist_shouldReturnResult() {
        String emailAddress = "hackerljm@gmail.com";
        String username = "림정묵";
        User newUser = User.create(username, emailAddress, "MyPassword!");
        userRepository.save(newUser);

        User found = userRepository.findByEmailAddress("hackerljm@gmail.com")
                .orElse(null);

        assertEquals(username, found.getUsername());
    }

    @Test
    void findByUsername_notExist_shouldReturnEmptyResult() {
        String username = "임ㅈ어묵";
        User user = userRepository.findByUsername(username).orElse(null);
        assertNull(user);
    }

    @Test
    void findByUsername_exist_shouldReturnResult() {
        String emailAddress = "hackerljm@gmail.com";
        String username = "림정묵";

        User newUser = User.create(username, emailAddress, "MyPassword!");
        userRepository.save(newUser);

        User found = userRepository.findByUsername(username).orElse(null);
        assertEquals(emailAddress, found.getEmailAddress());
    }
}