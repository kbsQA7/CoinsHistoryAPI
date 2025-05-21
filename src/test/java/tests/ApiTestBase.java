package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import specs.ApiSpec;

public abstract class ApiTestBase {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = ApiSpec.BASE_URI;
        RestAssured.basePath = ApiSpec.BASE_PATH;
    }

    @Step("Базовый запрос")
    protected RequestSpecification baseSpec() {
        return ApiSpec.baseRequestSpec;
    }
}
