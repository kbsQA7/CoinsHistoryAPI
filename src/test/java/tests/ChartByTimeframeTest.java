package tests;

import io.restassured.response.Response;
import models.ChartResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Owner;
import utils.RandomUtils;
import utils.TestData;
import validators.ChartValidator;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class ChartByTimeframeTest extends ApiTestBase {


    @Test
    @Tag("positive")
    @DisplayName("График по таймфрейму: случайная монета и таймфрейм")
    @Owner("kbsQA7")
    void chartByRandomTimeFrame() {
        String coin = RandomUtils.randomCoin();
        String timeframe = RandomUtils.randomTimeFrame();

        ChartResponse model = step(
                String.format("Отправляем запрос с coin=%s и timeframe=%s", coin, timeframe),
                () -> given(baseSpec())
                        .queryParam("timeFrame", timeframe)
                        .when()
                        .get("/chart-by-timeframe/{coin}", coin)
                        .then()
                        .statusCode(200)
                        .extract().as(ChartResponse.class)
        );

        step("Проверяем корректность содержимого графика", () ->
                ChartValidator.validateSuccess(model)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Негативный тест: неверный таймфрейм")
    @Owner("kbsQA7")
    void chartByInvalidTimeFrame() {
        String coin = RandomUtils.randomCoin();

        Response resp = step(
                String.format("Отправляем запрос с некорректным таймфреймом: %s", TestData.INVALID_TIMEFRAME),
                () -> given(baseSpec())
                        .queryParam("timeFrame", TestData.INVALID_TIMEFRAME)
                        .when()
                        .get("/chart-by-timeframe/{coin}", coin)
                        .then()
                        .statusCode(400)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке таймфрейма", () ->
                ChartValidator.validateInvalidTimeFrame(resp)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Негативный тест: несуществующая монета")
    @Owner("kbsQA7")
    void chartByInvalidCoin() {
        String timeframe = RandomUtils.randomTimeFrame();

        Response resp = step(
                String.format("Отправляем запрос с несуществующей монетой: %s", TestData.INVALID_COIN),
                () -> given(baseSpec())
                        .queryParam("timeFrame", timeframe)
                        .when()
                        .get("/chart-by-timeframe/{coin}", TestData.INVALID_COIN)
                        .then()
                        .statusCode(404)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке для несуществующей монеты", () ->
                ChartValidator.validateInvalidCoin(resp)
        );
    }
}
