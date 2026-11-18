package io.github.sonyfrid.wiki.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Ответ поиска /search/page и /search/title. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchResults(List<Page> pages) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Page(long id, String key, String title, String excerpt, String description) {
    }
}
