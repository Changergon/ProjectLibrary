package org.example.library.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JournalControllerContractTest {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createJournal_shouldConformToOpenApiContract() {
        String requestBody = """
                {
                  "title": "Test Journal",
                  "publisher": "Test Publisher",
                  "publicationYear": 2023,
                  "issueNumber": 1
                }""";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/journals")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Journal"))
                .body("publisher", equalTo("Test Publisher"));
    }
}
