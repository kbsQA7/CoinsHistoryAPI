package validators;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.FearIndexResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class FearIndexValidator {

    @Step("Валидация корректности данных индекса страха")
    public static void validate(FearIndexResponse model, long fromMs, long toMs) {
        assertThat(model.getStatus())
                .as("status должен быть OK")
                .isEqualTo("OK");

        assertThat(model.getData())
                .as("data не должен быть пустым")
                .isNotEmpty();

        assertThat(model.getFirstTime())
                .as("Первый timestamp должен быть в интервале от from")
                .isBetween(fromMs, toMs);

        assertThat(model.getLastTime())
                .as("Последний timestamp должен быть в интервале до to")
                .isBetween(fromMs, toMs);
    }

    @Step("Проверка ошибки: некорректный интервал (from позже to)")
    public static void validateInvalidInterval(Response resp) {
        String error = resp.getBody().asString();
        assertThat(error)
                .as("Ожидалась ошибка о порядке дат")
                .contains("Provide")
                .contains("date value")
                .contains("before");
    }

    @Step("Проверка ошибки: пропущен параметр 'to'")
    public static void validateMissingTo(Response resp) {
        String error = resp.getBody().asString();
        assertThat(error)
                .as("Ожидалась ошибка о пропущенном параметре 'to'")
                .containsIgnoringCase("to")
                .containsIgnoringCase("required")
                .containsIgnoringCase("not present");
    }

    @Step("Проверка ошибки: пропущен параметр 'from'")
    public static void validateMissingFrom(Response resp) {
        String error = resp.getBody().asString();
        assertThat(error)
                .as("Ожидалась ошибка о пропущенном параметре 'from'")
                .containsIgnoringCase("from")
                .containsIgnoringCase("required");
    }
}

