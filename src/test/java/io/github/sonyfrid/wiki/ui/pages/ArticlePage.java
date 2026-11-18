package io.github.sonyfrid.wiki.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.github.sonyfrid.wiki.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Статья в десктопной теме Vector 2022. */
public class ArticlePage {
    private final SelenideElement heading = $("#firstHeading");
    private final ElementsCollection tocLinks = $$("#vector-toc .vector-toc-list-item > a");
    private final SelenideElement infobox = $(".infobox");
    private final SelenideElement languageButton = $("#p-lang-btn");

    @Step("Открыть статью «{title}» ({lang})")
    public ArticlePage open(String lang, String title) {
        com.codeborne.selenide.Selenide.open(Config.baseUrl(lang) + "/wiki/" + title);
        heading.shouldBe(visible);
        return this;
    }

    public SelenideElement heading() {
        return heading;
    }

    public ElementsCollection toc() {
        return tocLinks;
    }

    public SelenideElement infobox() {
        return infobox;
    }

    @Step("Перейти к разделу «{section}» через оглавление")
    public ArticlePage goToSection(String section) {
        tocLinks.findBy(text(section)).click();
        return this;
    }

    @Step("Переключить язык статьи на {code}")
    public ArticlePage switchLanguage(String code) {
        languageButton.click();
        $$("li.interwiki-" + code).filter(visible).first().$("a").click();
        heading.shouldBe(visible);
        return this;
    }

}
