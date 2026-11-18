package io.github.sonyfrid.wiki.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.sonyfrid.wiki.config.Config;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

/** Общая настройка Selenide: headless Chrome, ожидания, скриншоты и шаги в Allure. */
@Tag("ui")
public abstract class BaseUiTest {

    @BeforeAll
    static void configure() {
        Configuration.browser = "chrome";
        Configuration.headless = Boolean.parseBoolean(Config.get("selenide.headless"));
        Configuration.timeout = Long.parseLong(Config.get("selenide.timeout"));
        Configuration.pageLoadStrategy = "eager";
        Configuration.reportsFolder = "target/selenide-reports";
    }

    @BeforeEach
    void listener() {
        Configuration.browserSize = browserSize();
        Configuration.browserCapabilities = capabilities();
        SelenideLogger.addListener("allure", new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @AfterEach
    void tearDown() {
        SelenideLogger.removeListener("allure");
        Selenide.closeWebDriver();
    }

    /** Размер окна; мобильные тесты переопределяют. */
    protected String browserSize() {
        return "1366x900";
    }

    /** Дополнительные возможности браузера; мобильные тесты включают эмуляцию телефона. */
    protected org.openqa.selenium.MutableCapabilities capabilities() {
        return new org.openqa.selenium.chrome.ChromeOptions();
    }
}
