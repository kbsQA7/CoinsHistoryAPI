package tests;

import io.qameta.allure.Owner;
import io.restassured.response.Response;
import models.PriceHistoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.RandomUtils;
import utils.TestData;
import validators.PriceHistoryValidator;


import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class PriceHistoryTest extends ApiTestBase {

    @Test
    @DisplayName("История цен: случайная монета, случайная дата")
    @Owner("kbsQA7")
    void randomCoinRandomDate() {
        String coin = RandomUtils.randomCoin();
        String date = RandomUtils.randomDate(2011, 2013);

        PriceHistoryResponse model = step(String.format("Запрос цены coin=%s на дату=%s", coin, date), () ->
                given(baseSpec())
                        .queryParam("date", date)
                        .when()
                        .get("/price/{coin}", coin)
                        .then()
                        .statusCode(200)
                        .extract().as(PriceHistoryResponse.class)
        );


        step("Проверяем статус, наличие данных и флаг failure", () ->
                PriceHistoryValidator.validateSuccess(model)
        );
    }

    @Test
    @DisplayName("История цен: несуществующая монета")
    @Owner("kbsQA7")
    void nonExistingCoin() {
        String fakeCoin = TestData.INVALID_COIN;
        String date = RandomUtils.randomDate(2022, 2024);

        Response resp = step("Запрос цены для несуществующей монеты: " + fakeCoin, () ->
                given(baseSpec())
                        .queryParam("date", date)
                        .when()
                        .get("/price/{coin}", fakeCoin)
                        .then()
                        .statusCode(404)
                        .extract().response()
        );

        step("Проверяем сообщение об ошибке", () ->
                PriceHistoryValidator.validateNotFound(resp)
        );
    }

    @Test
    @DisplayName("История цен: дата до 2013 года")
    @Owner("kbsQA7")
    void historyWithOldDate() {
        String coin = RandomUtils.randomCoin();
        String oldDate = RandomUtils.randomDate(2000, 2012);

        Response resp = step(String.format("Запрос цены coin=%s на слишком старую дату=%s", coin, oldDate), () ->
                given(baseSpec())
                        .queryParam("date", oldDate)
                        .when()
                        .get("/price/{coin}", coin)
                        .then()
                        .statusCode(404)
                        .extract().response()
        );

        step("Проверяем, что ответ содержит информацию об отсутствии данных", () ->
                PriceHistoryValidator.validateNotFound(resp)
        );
    }
}
