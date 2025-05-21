package validators;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.ChartResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ChartValidator {

    @Step("Проверяем успешный ответ: статус OK, есть данные, цены всех валют положительные")
    public static void validateSuccess(ChartResponse model) {
        assertThat(model.getStatus()).isEqualTo("OK");
        assertThat(model.getData()).isNotEmpty();

        model.getData().forEach(point -> {
            assertThat(point.getTimestamp()).isPositive();
            Map<String, Double> prices = point.getPrice();
            assertThat(prices).isNotEmpty();

            prices.forEach((currency, value) ->
                    assertThat(value)
                            .as("Price in %s at timestamp %d", currency, point.getTimestamp())
                            .isNotNull()
                            .isGreaterThan(0.0)
            );
        });
    }

    @Step("Проверяем, что вернулся ответ об ошибке: неверный таймфрейм")
    public static void validateInvalidTimeFrame(Response resp) {
        String failureDetails = resp.jsonPath().getString("failureDetails");
        assertThat(failureDetails)
                .isNotNull()
                .contains("Timeframe field must be one of");
    }

    @Step("Проверяем, что вернулся ответ об ошибке: монета не найдена")
    public static void validateInvalidCoin(Response resp) {
        String failureDetails = resp.jsonPath().getString("failureDetails");
        assertThat(failureDetails)
                .isNotNull()
                .containsIgnoringCase("not found");
    }

    @Step("Проверяем успешный ответ: статус OK, timestamp в пределах from–to (интервал)")
    public static void validateIntervalChart(ChartResponse model, long from, long to) {
        assertThat(model.getStatus()).isEqualTo("OK");
        assertThat(model.getData()).isNotEmpty();

        model.getData().forEach(point -> {
            assertThat(point.getTimestamp())
                    .as("timestamp должен быть в пределах from–to")
                    .isBetween(from, to);

            assertThat(point.getPrice()).isNotEmpty();
            point.getPrice().forEach((currency, value) ->
                    assertThat(value)
                            .as("Price in %s at %d", currency, point.getTimestamp())
                            .isNotNull()
                            .isGreaterThan(0.0)
            );
        });
    }

    @Step("Проверяем, что вернулся ответ об ошибке: from позже to")
    public static void validateFromAfterTo(Response resp) {
        String failureDetails = resp.jsonPath().getString("failureDetails");
        assertThat(failureDetails)
                .isNotBlank()
                .contains("date value")
                .contains("before");
    }

    @Step("Проверяем, что вернулся ответ об ошибке: неверный формат даты")
    public static void validateInvalidDateFormat(Response resp) {
        String failureDetails = resp.jsonPath().getString("failureDetails");
        assertThat(failureDetails)
                .isNotBlank()
                .contains("Failed to convert from type")
                .contains("for value");
    }
}


