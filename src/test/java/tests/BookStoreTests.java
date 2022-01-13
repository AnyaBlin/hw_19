package tests;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static filters.CustomLogFilter.customLogFilter;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookStoreTests {
    @BeforeAll
    static void setup() {

        RestAssured.baseURI = "https://demoqa.com";
        Configuration.baseUrl = "https://demoqa.com";

    }

    @Test
    void noLogsTest() {

        get("/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));

    }

    @Test
    void withAllLogsTest() {

        given()
                .get("/BookStore/v1/Books")
                .then()
                .log().all()
                .body("books", hasSize(greaterThan(0)));

    }

    @Test
    void authorizeTests() {
        String data = "{ \"userName\": \"Ann\", \"password\": \"1234567890Aa!\"}";
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .log().all()
                .post("Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));

    }
    @Test
    void authorizeTestsWithAllure() {
        String data = "{ \"userName\": \"Ann\", \"password\": \"1234567890Aa!\"}";
        given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(data)
                .when()
                .log().all()
                .post("Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));

    }
    @Test
    void authorizeTestsWitTemplate() {
        String data = "{ \"userName\": \"Ann\", \"password\": \"1234567890Aa!\"}";
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType("application/json")
                .body(data)
                .when()
                .log().all()
                .post("Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));

    }

}
