package io.github.sonyfrid.wiki.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.github.sonyfrid.wiki.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Статья в мобильной версии (тема Minerva): разделы свёрнуты, поиск за иконкой. */
public class MobileArticlePage {
    private final SelenideElement heading = $("#firstHeading, .mw-first-heading");
    private final ElementsCollection sectionHeadings = $$(".mw-parser-output .mw-heading2");
    private final SelenideElement searchIcon = $("#searchIcon");

    @Step("Открыть мобильную статью «{title}» ({lang})")
    public MobileArticlePage open(String lang, String title) {
        com.codeborne.selenide.Selenide.open(Config.mobileUrl(lang) + "/wiki/" + title);
        heading.shouldBe(visible);
        return this;
    }

    public SelenideElement heading() {
        return heading;
    }

    public ElementsCollection sections() {
        return sectionHeadings;
    }

    public SelenideElement searchIcon() {
        return searchIcon;
    }
}
