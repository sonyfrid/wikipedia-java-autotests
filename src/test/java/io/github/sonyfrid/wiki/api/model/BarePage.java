package io.github.sonyfrid.wiki.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Метаданные страницы из /page/{title}/bare. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BarePage(
        long id,
        String key,
        String title,
        Latest latest,
        @JsonProperty("content_model") String contentModel,
        License license,
        @JsonProperty("html_url") String htmlUrl) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Latest(long id, String timestamp) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record License(String url, String title) {
    }
}
