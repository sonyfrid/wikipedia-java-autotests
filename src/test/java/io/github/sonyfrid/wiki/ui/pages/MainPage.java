package io.github.sonyfrid.wiki.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.github.sonyfrid.wiki.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Главная страница и поле поиска в шапке (одинаковое на всех страницах Vector 2022). */
public class MainPage {
    // после загрузки Vue пересоздаёт поле без id, а на узком экране или в старой разметке поле другое:
    // берём любое видимое поле поиска по name, а свёрнутое сначала раскрываем значком лупы
    private final SelenideElement searchInput = $$("input[name=search]").findBy(visible);
    private final ElementsCollection searchToggle = $$("a.search-toggle, button.search-toggle, #p-search > a");
    private final ElementsCollection suggestions = $$(".cdx-typeahead-search__menu .cdx-menu-item");

    @Step("Открыть главную страницу Википедии ({lang})")
    public MainPage open(String lang) {
        com.codeborne.selenide.Selenide.open(Config.baseUrl(lang) + "/wiki/Main_Page");
        if (!$$("input[name=search]").filter(visible).isEmpty()) {
            return this;
        }
        SelenideElement toggle = searchToggle.findBy(visible);
        if (toggle.exists()) {
            toggle.click();
        }
        searchInput.shouldBe(visible);
        return this;
    }

    @Step("Ввести «{query}» в поиск")
    public MainPage type(String query) {
        searchInput.click();
        searchInput.sendKeys(query);
        return this;
    }

    public ElementsCollection suggestions() {
        return suggestions;
    }

    @Step("Найти «{query}» и нажать Enter")
    public void search(String query) {
        type(query);
        searchInput.pressEnter();
    }

}
