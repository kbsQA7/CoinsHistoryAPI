package validators;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.PriceHistoryResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceHistoryValidator {

    @Step("Проверяем успешный ответ: status = OK, есть данные, failure = false")
    public static void validateSuccess(PriceHistoryResponse model) {
        assertThat(model.getStatus()).isEqualTo("OK");
        assertThat(model.getData()).isNotEmpty();
        assertThat(model.isFailure()).isFalse();
        assertThat(model.getFailureDetails()).isNull();
    }

    @Step("Проверяем, что вернулся ответ с ошибкой (not found)")
    public static void validateNotFound(Response resp) {
        PriceHistoryResponse model = resp.as(PriceHistoryResponse.class);

        assertThat(model.isFailure()).isTrue();
        assertThat(model.getFailureDetails())
                .as("Ожидалось, что failureDetails содержит описание ошибки")
                .isNotBlank()
                .containsIgnoringCase("not found");
    }
}