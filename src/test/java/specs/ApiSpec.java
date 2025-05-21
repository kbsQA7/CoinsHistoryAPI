package specs;

import config.ConfigLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

import static helpers.CustomAllureService.withCustomTemplates;

public class ApiSpec {
    private static final ConfigLoader cfg = ConfigFactory.create(ConfigLoader.class);
    public static final String BASE_URI = cfg.baseUri();
    public static final String BASE_PATH = cfg.basePath();

    public static final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setBasePath(BASE_PATH)
            .addHeader("x-dropstab-api-key", cfg.apiKey())
            .addFilter(withCustomTemplates())
            .addFilter(new AllureRestAssured())
            .addFilter(new RequestLoggingFilter(LogDetail.METHOD))
            .addFilter(new ResponseLoggingFilter(LogDetail.BODY))
            .build();
}