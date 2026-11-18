package io.github.sonyfrid.wiki.api;

import io.github.sonyfrid.wiki.api.model.PageSummary;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Epic("REST API")
@Feature("Краткое описание статьи: /page/summary")
class SummaryApiTest {

    private static PageSummary summary(String lang, String title) {
        return given().spec(WikiApi.restV1(lang))
                .when().get("/page/summary/{title}", title)
                .then().spec(WikiApi.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/summary.json"))
                .extract().as(PageSummary.class);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Статья про Java: схема ответа, название, Wikidata, ссылки")
    void java_article_summary_matches_schema_and_content() {
        PageSummary s = summary("en", "Java_(programming_language)");

        assertThat(s.type()).isEqualTo("standard");
        assertThat(s.title()).isEqualTo("Java (programming language)");
        assertThat(s.wikibaseItem()).isEqualTo("Q251");
        assertThat(s.lang()).isEqualTo("en");
        assertThat(s.dir()).isEqualTo("ltr");
        assertThat(s.extract()).containsIgnoringCase("programming language");
        assertThat(s.contentUrls().desktop().page()).isEqualTo("https://en.wikipedia.org/wiki/Java_(programming_language)");
        assertThat(s.contentUrls().mobile().page()).endsWith("/wiki/Java_(programming_language)");
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "Earth, Earth, Q2",
            "Moscow, Moscow, Q649",
            "Selenium_(software), Selenium (software), Q752522",
    })
    @DisplayName("Название в URL нормализуется, у статьи свой элемент Wikidata")
    void title_is_normalized(String urlTitle, String expectedTitle, String wikidata) {
        PageSummary s = summary("en", urlTitle);

        assertThat(s.title()).isEqualTo(expectedTitle);
        assertThat(s.wikibaseItem()).isEqualTo(wikidata);
        assertThat(s.pageid()).isPositive();
    }

    @Test
    @DisplayName("Редирект USA разрешается в статью United States")
    void redirect_is_resolved() {
        PageSummary s = summary("en", "USA");

        assertThat(s.title()).isEqualTo("United States");
        assertThat(s.type()).isEqualTo("standard");
    }

    @Test
    @DisplayName("Многозначное слово Mercury помечено как disambiguation")
    void disambiguation_page_has_its_type() {
        assertThat(summary("en", "Mercury").type()).isEqualTo("disambiguation");
    }

    @Test
    @DisplayName("Русская и английская статьи о Москве ссылаются на один элемент Wikidata")
    void same_wikidata_item_across_languages() {
        PageSummary ru = summary("ru", "Москва");
        PageSummary en = summary("en", "Moscow");

        assertThat(ru.lang()).isEqualTo("ru");
        assertThat(ru.title()).isEqualTo("Москва");
        assertThat(ru.wikibaseItem()).isEqualTo(en.wikibaseItem());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Несуществующая статья: 404 и JSON с кодом ошибки")
    void missing_article_returns_404() {
        given().spec(WikiApi.restV1("en"))
                .when().get("/page/summary/{title}", "Nonexistent_page_zzqx_12345")
                .then().statusCode(404)
                .contentType("application/json")
                .body("status", org.hamcrest.Matchers.equalTo(404));
    }

    @Test
    @DisplayName("Случайная статья: 303 на /page/summary/, по ссылке валидный ответ")
    void random_article_redirects_to_summary() {
        Response redirect = given().spec(WikiApi.restV1("en")).redirects().follow(false)
                .when().get("/page/random/summary");

        assertThat(redirect.statusCode()).isEqualTo(303);
        String location = redirect.header("Location");
        assertThat(location).contains("/page/summary/");

        String title = location.substring(location.lastIndexOf('/') + 1);
        PageSummary s = summary("en", java.net.URLDecoder.decode(title, java.nio.charset.StandardCharsets.UTF_8));
        assertThat(s.title()).isNotBlank();
    }
}
