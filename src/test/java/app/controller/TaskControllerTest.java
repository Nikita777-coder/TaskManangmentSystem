package app.controller;

import app.dto.auth.SignUpRequest;
import app.entity.TaskEntity;
import app.entity.taskattributes.TaskPriority;
import app.entity.taskattributes.TaskStatus;
import app.entity.userattributes.Role;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("create task test, ok test; make auth SignUpRequest(email=em@em.ru, role=ADMIN," +
            "password=JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T), set received" +
            "jwt to testRestTemplate and send TaskEntity{"+
            "  \"header\": \"string\",\n" +
            "  \"description\": \"string\",\n" +
            "  \"taskStatus\": \"WAIT\",\n" +
            "  \"taskPriority\": \"LOW\",\n" +
            "  \"authorEmail\": \"em@em.ru\"}")
    void createTask() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setRole(Role.ADMIN);
        signUpRequest.setPassword("JeHQr9maqNo54anWoRq%PaVCLgknZgb6gJqmGLio@yX$oC5zE3RibjFRz97ezc!DCZID!AFrO1T");
        signUpRequest.setEmail("em@em.ru");

        ResponseEntity<String> response = testRestTemplate.postForEntity("/auth/signup", signUpRequest, String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + response.getBody());
        headers.setContentType(MediaType.APPLICATION_JSON);

        TaskEntity taskEntity = TaskEntity.builder()
                .header("string")
                .description("string")
                .taskStatus(TaskStatus.WAIT)
                .taskPriority(TaskPriority.LOW)
                .authorEmail("em@em.ru")
                .build();

        HttpEntity<TaskEntity> requestEntity = new HttpEntity<>(taskEntity, headers);

        ResponseEntity<String> response1 = testRestTemplate.exchange(
                "/task",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        String res = response1.getBody().replaceAll("\"", "");
        assertDoesNotThrow(() -> UUID.fromString(res));
    }
}