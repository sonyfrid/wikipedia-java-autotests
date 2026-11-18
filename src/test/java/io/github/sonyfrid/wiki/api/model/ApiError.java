package io.github.sonyfrid.wiki.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Ошибка Core REST API: {"errorKey": ..., "httpCode": ..., "httpReason": ...}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(String errorKey, int httpCode, String httpReason) {
}
