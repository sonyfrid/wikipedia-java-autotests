package io.github.sonyfrid.wiki.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Страница результатов полнотекстового поиска Special:Search. */
public class SearchResultsPage {
    private final ElementsCollection results = $$(".mw-search-result-heading a");
    private final SelenideElement nothingFound = $(".mw-search-nonefound");

    public ElementsCollection results() {
        return results;
    }

    public SelenideElement nothingFound() {
        return nothingFound;
    }
}
