package io.github.sonyfrid.wiki.ui;

import io.github.sonyfrid.wiki.ui.pages.ArticlePage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@Epic("Веб-интерфейс")
@Feature("Статья")
class ArticleUiTest extends BaseUiTest {
    private final ArticlePage article = new ArticlePage();

    @Test
    @DisplayName("Оглавление: есть раздел Etymology, переход меняет якорь в адресе")
    void table_of_contents_navigation() {
        article.open("en", "Earth");

        article.toc().shouldHave(sizeGreaterThan(5));
        article.toc().shouldHave(itemWithText("Etymology"));
        article.goToSection("Etymology");
        webdriver().shouldHave(urlContaining("#Etymology"));
    }

    @Test
    @DisplayName("Карточка-инфобокс статьи видна и содержит данные")
    void infobox_is_shown() {
        article.open("en", "Earth");

        article.infobox().shouldBe(visible).shouldHave(text("Earth"));
    }

    @Test
    @DisplayName("Переключение языка: Earth → русская статья «Земля»")
    void switch_language_to_russian() {
        article.open("en", "Earth").switchLanguage("ru");

        article.heading().shouldHave(exactText("Земля"));
        $("html").shouldHave(attribute("lang", "ru"));
        webdriver().shouldHave(urlContaining("ru.wikipedia.org"));
    }
}
