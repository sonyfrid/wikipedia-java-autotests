package io.github.sonyfrid.wiki.api;

import io.github.sonyfrid.wiki.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

/**
 * Общие спецификации запросов. У Wikimedia есть правило: клиент должен представиться в User-Agent,
 * поэтому он задан явно. Запросов немного, нагрузку тесты не создают.
 */
public final class WikiApi {
    private WikiApi() {
    }

    private static RequestSpecBuilder base(String lang) {
        return new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl(lang))
                .addHeader("User-Agent", Config.userAgent())
                .setAccept(ContentType.JSON)
                .addFilter(new PoliteFilter())
                .addFilter(new AllureRestAssured())
                .log(LogDetail.URI);
    }

    /** REST API контента: /api/rest_v1 (summary, random). */
    public static RequestSpecification restV1(String lang) {
        return base(lang).setBasePath("/api/rest_v1").build();
    }

    /** Core REST API MediaWiki: /w/rest.php/v1 (поиск, страницы, языковые ссылки). */
    public static RequestSpecification core(String lang) {
        return base(lang).setBasePath("/w/rest.php/v1").build();
    }

    /** Успешный JSON-ответ за разумное время. */
    public static ResponseSpecification okJson() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(10_000L))
                .build();
    }
}
