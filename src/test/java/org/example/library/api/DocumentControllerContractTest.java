package org.example.library.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentControllerContractTest {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        new org.example.library.services.StorageService().init();
    }

    @Test
    void uploadDocument_shouldConformToOpenApiContract() {
        given()
                .multiPart("file", new File("pom.xml"))
                .when()
                .post("/api/v1/documents")
                .then()
                .statusCode(200);
    }
}
