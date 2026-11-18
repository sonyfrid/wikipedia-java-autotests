package io.github.sonyfrid.wiki.ui;

import io.github.sonyfrid.wiki.ui.pages.ArticlePage;
import io.github.sonyfrid.wiki.ui.pages.MainPage;
import io.github.sonyfrid.wiki.ui.pages.SearchResultsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.Selenide.webdriver;

@Epic("Веб-интерфейс")
@Feature("Поиск")
class SearchUiTest extends BaseUiTest {
    private final MainPage main = new MainPage();
    private final ArticlePage article = new ArticlePage();
    private final SearchResultsPage results = new SearchResultsPage();

    @ParameterizedTest(name = "{0}: «{1}»")
    @CsvSource({
            "en, Selenium (software), Selenium (software)",
            "ru, Тестирование программного обеспечения, Тестирование программного обеспечения",
    })
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Точное название в поиске сразу открывает статью")
    void exact_title_opens_article(String lang, String query, String heading) {
        main.open(lang).search(query);

        article.heading().shouldHave(exactText(heading));
    }

    @Test
    @DisplayName("Подсказки при вводе показывают Selenium (software)")
    void typeahead_suggestions() {
        main.open("en").type("Selenium");

        main.suggestions().shouldHave(sizeGreaterThan(2));
        // пункт подсказки = название + короткое описание, поэтому ищем по вхождению текста
        main.suggestions().findBy(text("Selenium (software)")).shouldBe(visible).shouldHave(text("software"));
    }

    @Test
    @DisplayName("Неточный запрос ведёт на страницу результатов")
    void fuzzy_query_shows_results() {
        main.open("en").search("selenium webdriver browser automation");

        webdriver().shouldHave(urlContaining("search="));
        results.results().shouldHave(sizeGreaterThan(0));
    }

    @Test
    @DisplayName("Бессмысленный запрос: сообщение «ничего не найдено»")
    void nothing_found_message() {
        main.open("en").search("zzqxnonexistentword12345");

        results.nothingFound().shouldBe(visible);
    }
}
