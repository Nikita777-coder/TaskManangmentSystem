package app.controller;

import app.dto.auth.SignInRequest;
import app.dto.auth.SignUpRequest;
import app.entity.userattributes.Role;
import app.repository.UserRepository;
import app.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    public void clearUserRepository() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("create user, ok test; give SignUpRequest(email=em@em.ru, role=ADMIN, " +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T); " +
            "login extracted from received jwt must be email from SignUpRequest")
    void createUser() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        String re = response.getBody();
        assertEquals("em@em.ru", jwtService.extractLogin(re));
    }

    @Test
    @DisplayName("create user with existing data; give 2 SignUpRequest(email=em@em.ru, role=ADMIN, " +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T); " +
            "the second query must return BadRequest")
    void createUserWithExistingData() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("enter test, ok test; give SignUpRequest(email=em@em.ru, role=ADMIN, " +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T) and then enter with same data; " +
            "must return jwt token with email as login from claims")
    void enter() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("em@em.ru");
        signInRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");

        testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signin", signInRequest, String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        String re = response.getBody();
        assertEquals("em@em.ru", jwtService.extractLogin(re));
    }

    @Test
    @DisplayName("enter test, email incorrect; give SignInRequest(email=em@em.ru, " +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T); " +
            "must return bad request code")
    void enterWithIncorrectEmail() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("em@em.ru");
        signInRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signin", signInRequest, String.class);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    @DisplayName("enter test, password incorrect; give SignUpRequest(email=em@em.ru, role=ADMIN," +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T)" +
            "and SignInRequest(email=em@em.ru, password=123); " +
            "must return bad request code with error message=\"password is invalid!\"")
    void enterWithIncorrectPassword() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("em@em.ru");
        signInRequest.setPassword("123");

        testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signin", signInRequest, String.class);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("password is invalid!", response.getBody());
    }
}