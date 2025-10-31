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
class BookControllerContractTest {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createBook_shouldConformToOpenApiContract() {
        String requestBody = """
                {
                  "title": "Test Book",
                  "author": "Test Author",
                  "isbn": "978-3-16-148410-0",
                  "description": "Test Description",
                  "publicationYear": 2023,
                  "pageCount": 100
                }""";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/books")
                .then()
                .statusCode(201)
                .body("title", equalTo("Test Book"))
                .body("author", equalTo("Test Author"));
    }
}
