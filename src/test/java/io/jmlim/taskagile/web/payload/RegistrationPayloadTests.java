package io.jmlim.taskagile.web.payload;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationPayloadTests {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("빈 페이로드가 실패했는지 확인.")
    void blankPayload() {
        RegistrationPayload payload = new RegistrationPayload();
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(3, violations.size());
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 페이로드 확인")
    void validatePayloadWithInvalidEmail() {
        RegistrationPayload payload = new RegistrationPayload();
        payload.setEmailAddress("BadEmailAddress");
        payload.setUsername("림정묵");
        payload.setPassword("myPassword");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("메일 주소가 100 보다 긴 페이로드 실패 확인")
    void payloadWithEmailAddressLongerThan100() {
        // The maximium allowed localPart is 64 characters
        // http://www.rfc-editor.org/errata_search.php?rfc=3696&eid=1690

        int maxLocalParthLength = 64;
        String localPart = RandomStringUtils.random(maxLocalParthLength, true, true);
        int usedLength = maxLocalParthLength + "@".length() + ".com".length();
        String domain = RandomStringUtils.random(101 - usedLength, true, true);

        System.out.println("localPart: " + localPart);
        System.out.println("domain: " + domain);

        RegistrationPayload payload = new RegistrationPayload();
        payload.setEmailAddress(localPart + "@" + domain + ".com");
        payload.setUsername("림정묵");
        payload.setPassword("MyPassword");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("2보다 짧은 사용자 이름으로 페이로드 유효성 검사")
    void payloadWithUsernameShorterThan2() {
        RegistrationPayload payload = new RegistrationPayload();
        String usernameTooShort = RandomStringUtils.random(1);
        payload.setUsername(usernameTooShort);
        payload.setPassword("MyPassword");
        payload.setEmailAddress("hackerljm@gmail.com");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("사용자 이름이 50보다 긴 페이로드 유효성 검사")
    void payloadWithUsernameLongerThan50() {
        RegistrationPayload payload = new RegistrationPayload();
        String usernameTooLong = RandomStringUtils.random(51);
        payload.setUsername(usernameTooLong);
        payload.setPassword("MyPassword");
        payload.setEmailAddress("hackerljm@gmail.com");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("패스워드가 6보다 작은 페이로드 유효성 검사")
    public void payloadWithPasswordShorterThan6() {
        String passwordTooShort = RandomStringUtils.random(5);
        RegistrationPayload payload = new RegistrationPayload();
        payload.setPassword(passwordTooShort);
        payload.setUsername("임저움ㄱ");
        payload.setEmailAddress("hackerljm@gmail.com");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("패스워드가 30보다 큰 페이로드 유효성 검사")
    public void payloadWithPasswordLongerThan30() {
        String passwordTooLong = RandomStringUtils.random(31);
        RegistrationPayload payload = new RegistrationPayload();
        payload.setPassword(passwordTooLong);
        payload.setUsername("임정묵");
        payload.setEmailAddress("hackerljm@gmail.com");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertEquals(1, violations.size());
    }
}
