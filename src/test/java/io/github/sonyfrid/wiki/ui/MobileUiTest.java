package io.github.sonyfrid.wiki.ui;

import com.codeborne.selenide.SelenideElement;
import io.github.sonyfrid.wiki.ui.pages.MobileArticlePage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Epic("Веб-интерфейс")
@Feature("Мобильная версия")
class MobileUiTest extends BaseUiTest {
    private final MobileArticlePage article = new MobileArticlePage();

    @Override
    protected String browserSize() {
        return "412x915";
    }

    /** Эмуляция телефона в Chrome: размер экрана, плотность пикселей, касания и мобильный User-Agent. */
    @Override
    protected MutableCapabilities capabilities() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", Map.of(
                "deviceMetrics", Map.of("width", 412, "height", 915, "pixelRatio", 2.625, "touch", true),
                "userAgent", "Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/130.0.0.0 Mobile Safari/537.36"));
        return options;
    }

    @Test
    @DisplayName("Мобильная статья: заголовок, иконка поиска, разделы")
    void mobile_article_layout() {
        article.open("en", "Earth");

        article.heading().shouldHave(exactText("Earth"));
        article.searchIcon().shouldBe(visible);
        article.sections().shouldHave(sizeGreaterThan(5));
    }

    @Test
    @DisplayName("Свёрнутый раздел раскрывается по нажатию на заголовок")
    void collapsed_section_expands_on_tap() {
        article.open("en", "Earth");

        SelenideElement toggle = article.sections().first().$(".mf-collapsible-toggle");
        SelenideElement content = $("#" + toggle.getAttribute("aria-controls"));

        toggle.shouldHave(attribute("aria-expanded", "false"));
        // свёрнутый раздел помечен hidden="until-found": текст есть, но не показан
        content.shouldHave(attribute("hidden"));
        article.sections().first().click();
        toggle.shouldHave(attribute("aria-expanded", "true"));
        content.shouldNotHave(attribute("hidden"));
    }
}
