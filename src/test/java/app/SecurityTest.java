package app;

import app.dto.auth.SignUpRequest;
import app.entity.userattributes.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("security test, request to open api; make request to auth/signup with some valid body; " +
            "must return ok status")
    void makeRequestToOpenApi() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    @DisplayName("security test, request to close api; make request to /user with some valid body; " +
            "must return forbidden status")
    void makeRequestToCloseApi() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/auth/signup", String.class);
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
    }
}
