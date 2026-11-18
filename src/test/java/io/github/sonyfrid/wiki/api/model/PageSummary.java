package io.github.sonyfrid.wiki.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Краткое описание статьи из /page/summary/{title}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PageSummary(
        String type,
        String title,
        long pageid,
        String lang,
        String dir,
        String extract,
        String description,
        @JsonProperty("wikibase_item") String wikibaseItem,
        @JsonProperty("content_urls") ContentUrls contentUrls) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentUrls(Urls desktop, Urls mobile) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Urls(String page) {
    }
}
