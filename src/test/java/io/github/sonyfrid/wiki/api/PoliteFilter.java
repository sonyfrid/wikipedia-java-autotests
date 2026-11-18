package io.github.sonyfrid.wiki.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Вежливость к публичному API: не чаще одного запроса в PAUSE_MS и один повтор после 429,
 * выждав Retry-After. Тестам это не мешает, а Wikimedia не приходится нас ограничивать.
 */
public class PoliteFilter implements Filter {
    private static final long PAUSE_MS = 1500;
    private static long lastRequestAt = 0;

    private static synchronized void waitTurn() {
        long wait = lastRequestAt + PAUSE_MS - System.currentTimeMillis();
        if (wait > 0) {
            sleep(wait);
        }
        lastRequestAt = System.currentTimeMillis();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Response filter(FilterableRequestSpecification req, FilterableResponseSpecification res, FilterContext ctx) {
        waitTurn();
        Response response = ctx.next(req, res);
        if (response.statusCode() == 429 && !RETRYING.get()) {
            String retryAfter = response.header("Retry-After");
            long seconds = retryAfter != null && retryAfter.matches("\\d+") ? Math.min(Long.parseLong(retryAfter), 60) : 10;
            sleep(seconds * 1000);
            RETRYING.set(true);
            try {
                // повтор тем же запросом через все фильтры (Allure покажет обе попытки)
                response = ctx.send(req);
            } finally {
                RETRYING.set(false);
            }
        }
        return response;
    }

    private static final ThreadLocal<Boolean> RETRYING = ThreadLocal.withInitial(() -> false);
}
