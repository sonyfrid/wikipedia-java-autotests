package io.github.sonyfrid.wiki.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Одна языковая версия статьи из /page/{title}/links/language. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LanguageLink(String code, String name, String key, String title) {
}
