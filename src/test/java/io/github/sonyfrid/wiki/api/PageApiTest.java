package io.github.sonyfrid.wiki.api;

import io.github.sonyfrid.wiki.api.model.ApiError;
import io.github.sonyfrid.wiki.api.model.BarePage;
import io.github.sonyfrid.wiki.api.model.LanguageLink;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Epic("REST API")
@Feature("Страница: /page/{title}")
class PageApiTest {

    @Test
    @DisplayName("Метаданные страницы Earth: схема, вики-разметка, лицензия CC BY-SA")
    void bare_page_metadata() {
        BarePage p = given().spec(WikiApi.core("en"))
                .when().get("/page/{title}/bare", "Earth")
                .then().spec(WikiApi.okJson())
                .body(matchesJsonSchemaInClasspath("schemas/bare.json"))
                .extract().as(BarePage.class);

        assertThat(p.title()).isEqualTo("Earth");
        assertThat(p.contentModel()).isEqualTo("wikitext");
        assertThat(p.license().title()).contains("Creative Commons").contains("Share Alike");
        assertThat(Instant.parse(p.latest().timestamp())).isBefore(Instant.now());
        assertThat(p.htmlUrl()).endsWith("/page/Earth/html");
    }

    @Test
    @DisplayName("Несуществующая страница: 404 rest-nonexistent-title")
    void missing_page_error_body() {
        ApiError e = given().spec(WikiApi.core("en"))
                .when().get("/page/{title}/bare", "Nonexistent_zzqx_1")
                .then().statusCode(404)
                .extract().as(ApiError.class);

        assertThat(e.errorKey()).isEqualTo("rest-nonexistent-title");
        assertThat(e.httpReason()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Языковые версии Earth: есть русская «Земля», коды уникальны")
    void language_links() {
        List<LanguageLink> links = Arrays.asList(given().spec(WikiApi.core("en"))
                .when().get("/page/{title}/links/language", "Earth")
                .then().spec(WikiApi.okJson())
                .extract().as(LanguageLink[].class));

        assertThat(links).hasSizeGreaterThan(100);
        assertThat(links).filteredOn(l -> l.code().equals("ru"))
                .singleElement()
                .extracting(LanguageLink::title).isEqualTo("Земля");
        assertThat(links).extracting(LanguageLink::code).doesNotHaveDuplicates();
    }
}
