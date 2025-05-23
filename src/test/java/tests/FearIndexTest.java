package tests;

import io.qameta.allure.Owner;
import io.restassured.response.Response;
import models.FearIndexResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import utils.IntervalUtils;
import utils.TestData;
import validators.FearIndexValidator;



import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class FearIndexTest extends ApiTestBase {

    @Test
    @Tag("positive")
    @DisplayName("Индекс страха: случайный интервал дат")
    @Owner("kbsQA7")
    void randomFearIndexInterval() {
        IntervalUtils interval = IntervalUtils.random(90);

        FearIndexResponse model = step(String.format("Отправляем запрос с from=%s и to=%s", interval.from, interval.to), () ->
                given(baseSpec())
                        .queryParam("from", interval.from)
                        .queryParam("to", interval.to)
                        .when()
                        .get("/fear-index")
                        .then()
                        .statusCode(200)
                        .extract().as(FearIndexResponse.class)
        );

        step("Проверяем корректность данных и границ интервала", () ->
                FearIndexValidator.validate(model, interval.expectedFrom, interval.expectedTo)
        );
    }


    @Test
    @Tag("negative")
    @DisplayName("Индекс страха: некорректный интервал — from позже to")
    @Owner("kbsQA7")
    void fearIndexInvalidInterval() {
        String from = TestData.FUTURE_FROM_DATE;
        String to = TestData.FUTURE_TO_DATE;

        Response resp = step(String.format("Отправляем запрос с некорректным интервалом: from=%s позже чем, to=%s", from, to), () ->
                given(baseSpec())
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .when()
                        .get("/fear-index")
                        .then()
                        .statusCode(400)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке", () ->
                FearIndexValidator.validateInvalidInterval(resp)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Индекс страха: пропущен параметр 'to'")
    @Owner("kbsQA7")
    void fearIndexMissingToParam() {
        String from = TestData.MISSING_TO_FROM_DATE;

        Response resp = step("Отправляем запрос только с параметром from=" + from, () ->
                given(baseSpec())
                        .queryParam("from", from)
                        .when()
                        .get("/fear-index")
                        .then()
                        .statusCode(400)
                        .extract().response()
        );


        step("Проверяем ошибку об отсутствии 'to'", () ->
                FearIndexValidator.validateMissingTo(resp)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Индекс страха: отсутствует параметр 'from'")
    @Owner("kbsQA7")
    void fearIndexMissingFromParam() {
        Response resp = step("Отправляем запрос без параметров", () ->
                given(baseSpec())
                        .when()
                        .get("/fear-index")
                        .then()
                        .statusCode(400)
                        .extract().response()
        );


        step("Проверяем ошибку об отсутствии 'from'", () ->
                FearIndexValidator.validateMissingFrom(resp)
        );
    }
}
