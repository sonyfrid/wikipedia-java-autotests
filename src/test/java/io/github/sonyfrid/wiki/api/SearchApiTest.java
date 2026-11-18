package io.github.sonyfrid.wiki.api;

import io.github.sonyfrid.wiki.api.model.ApiError;
import io.github.sonyfrid.wiki.api.model.SearchResults;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Epic("REST API")
@Feature("Поиск: /search/page и /search/title")
class SearchApiTest {

    private static SearchResults search(String lang, String path, String q, int limit) {
        return given().spec(WikiApi.core(lang))
                .queryParam("q", q)
                .queryParam("limit", limit)
                .when().get(path)
                .then().spec(WikiApi.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/search.json"))
                .extract().as(SearchResults.class);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Полнотекстовый поиск находит статью про Selenium как ПО")
    void full_text_search_finds_article() {
        SearchResults r = search("en", "/search/page", "selenium", 5);

        assertThat(r.pages()).extracting(SearchResults.Page::title).contains("Selenium (software)");
        assertThat(r.pages()).allSatisfy(p -> {
            assertThat(p.id()).isPositive();
            assertThat(p.key()).doesNotContain(" ");
        });
    }

    @ParameterizedTest(name = "limit={0}")
    @ValueSource(ints = {1, 5, 10})
    @DisplayName("Параметр limit ограничивает число результатов")
    void limit_is_respected(int limit) {
        assertThat(search("en", "/search/page", "testing", limit).pages()).hasSize(limit);
    }

    @Test
    @DisplayName("Подсказки по названию: Mosc → Moscow первым, все про Москву")
    void title_prefix_search() {
        SearchResults r = search("en", "/search/title", "Mosc", 5);

        assertThat(r.pages()).isNotEmpty();
        assertThat(r.pages().get(0).title()).isEqualTo("Moscow");
        assertThat(r.pages()).allSatisfy(p -> assertThat(p.title()).containsIgnoringCase("Mosc"));
    }

    @Test
    @DisplayName("Поиск по-русски в русской Википедии")
    void cyrillic_search_in_russian_wikipedia() {
        SearchResults r = search("ru", "/search/page", "автоматизированное тестирование", 5);

        assertThat(r.pages()).isNotEmpty();
        assertThat(r.pages()).anySatisfy(p -> assertThat(p.title()).containsIgnoringCase("тестирование"));
    }

    @Test
    @DisplayName("Запрос без результатов: 200 и пустой список, не ошибка")
    void nothing_found_is_empty_list() {
        assertThat(search("en", "/search/page", "zzqxnonexistentword12345", 5).pages()).isEmpty();
    }

    @Test
    @DisplayName("Без параметра q: 400 missingparam")
    void missing_query_is_bad_request() {
        ApiError e = given().spec(WikiApi.core("en"))
                .queryParam("limit", 3)
                .when().get("/search/page")
                .then().statusCode(400)
                .extract().as(ApiError.class);

        assertThat(e.errorKey()).isEqualTo("missingparam");
        assertThat(e.httpCode()).isEqualTo(400);
    }

    @ParameterizedTest(name = "limit={0}")
    @ValueSource(ints = {0, 101})
    @DisplayName("limit вне диапазона 1..100: 400")
    void limit_out_of_range_is_bad_request(int limit) {
        given().spec(WikiApi.core("en"))
                .queryParam("q", "java")
                .queryParam("limit", limit)
                .when().get("/search/page")
                .then().statusCode(400);
    }
}
