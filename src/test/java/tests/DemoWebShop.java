package tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static filters.CustomLogFilter.customLogFilter;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemoWebShop {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/";
    }

    @Tag("demowebshop")
    @Test
    void addToCard() {
        step("Добавляем товар в корзину и проверяем количество", () -> {
            String data = "product_attribute_72_5_18=53&" +
                    "product_attribute_72_6_19=54&" +
                    "product_attribute_72_3_20=57&" +
                    "addtocart_72.EnteredQuantity=1";
            Response response =
                    given()
                            .filter(customLogFilter().withCustomTemplates())
                            .log().all()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body(data)
                            .when()
                            .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                            .then()
                            .statusCode(200)
                            .body("updatetopcartsectionhtml", is("(1)"))
                            .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                            .extract().response();

            System.out.println("Response " + response.asString());
        });
    }
    @Tag("demowebshop")
    @Test
    void loginWithCookieTest() {
        String login = "ann@qa.guru";
        String password = "ann@qa.guru";

        step("Авторизация с помощью cookie", () -> {
            String authorizationCookie = given()
                    .filter(customLogFilter().withCustomTemplates())
                    .log().all()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .formParam("Email", login)
                    .formParam("Password", password)
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(302)
                    .extract()
                    .cookie("NOPCOMMERCE.AUTH");

            step("Открываем минимальный контент", () ->
                    open("Themes/DefaultClean/Content/images/logo.png"));

            step("Устанавливаем куку", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });
        step("Открываем главную страницу", () ->
                open("http://demowebshop.tricentis.com"));

        step("Проверяем успешную авторизацию", () -> {
            $(".account").shouldHave(text(login));
        });
    }
}

