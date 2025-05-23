package tests;

import io.restassured.response.Response;
import models.ChartResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Owner;
import utils.RandomUtils;
import utils.IntervalUtils;
import utils.TestData;
import validators.ChartValidator;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

@ExtendWith({AllureJunit5.class})
public class ChartByIntervalTest extends ApiTestBase {


    @Test
    @Tag("positive")
    @DisplayName("График по интервалу: случайная монета и интервал")
    @Owner("kbsQA7")
    void chartByIntervalRandomCoin() {
        IntervalUtils interval = IntervalUtils.random(60);
        String coin = RandomUtils.randomCoin();

        ChartResponse model = step(String.format("Отправляем запрос с coin=%s, from=%s, to=%s", coin, interval.from, interval.to), () ->
                given(baseSpec())
                        .queryParam("from", interval.from)
                        .queryParam("to", interval.to)
                        .when()
                        .get("/chart-by-interval/{coin}", coin)
                        .then()
                        .statusCode(200)
                        .extract().as(ChartResponse.class)
        );

        step("Проверяем содержимое графика (интервал)", () ->
                ChartValidator.validateIntervalChart(model, interval.expectedFrom, interval.expectedTo)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Негативный тест: несуществующая монета")
    @Owner("kbsQA7")
    void chartByIntervalInvalidCoin() {
        IntervalUtils interval = IntervalUtils.random(60);

        Response resp = step(String.format("Запрос с несуществующей монетой %s", TestData.INVALID_COIN), () ->
                given(baseSpec())
                        .queryParam("from", interval.from)
                        .queryParam("to", interval.to)
                        .when()
                        .get("/chart-by-interval/{coin}", TestData.INVALID_COIN)
                        .then()
                        .statusCode(404)
                        .extract().response()
        );

        step("Проверяем ошибку для несуществующей монеты", () ->
                ChartValidator.validateInvalidCoin(resp)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Негативный тест: from позже to")
    @Owner("kbsQA7")
    void chartByIntervalFromAfterTo() {
        IntervalUtils interval = IntervalUtils.random(60);
        String coin = RandomUtils.randomCoin();
        String from = interval.to;
        String to = interval.from;

        Response resp = step(String.format("Отправляем запрос с from позже to: from=%s, to=%s", from, to), () ->
                given(baseSpec())
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .when()
                        .get("/chart-by-interval/{coin}", coin)
                        .then()
                        .statusCode(400)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке для некорректного интервала", () ->
                ChartValidator.validateFromAfterTo(resp)
        );
    }

    @Test
    @Tag("negative")
    @DisplayName("Негативный тест: невалидный формат даты")
    @Owner("kbsQA7")
    void chartByIntervalInvalidDateFormat() {
        String invalidFrom = TestData.INVALID_DATE_FROM;
        String validTo = TestData.VALID_DATE_TO;
        String coin = RandomUtils.randomCoin();

        Response resp = step("Отправляем запрос с невалидной датой: from=" + invalidFrom, () ->
                given(baseSpec())
                        .queryParam("from", invalidFrom)
                        .queryParam("to", validTo)
                        .when()
                        .get("/chart-by-interval/{coin}", coin)
                        .then()
                        .statusCode(400)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке при неверной дате", () ->
                ChartValidator.validateInvalidDateFormat(resp)
        );
    }
}
