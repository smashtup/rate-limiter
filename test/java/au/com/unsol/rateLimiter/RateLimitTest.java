package au.com.unsol.rateLimiter;

import org.junit.Test;

import static au.com.unsol.rateLimiter.RateLimitConfig.DEFAULT_RATE_LIMIT_STRATEGY;
import static au.com.unsol.rateLimiter.RateLimitConfig.MILLISECONDS_IN_SECOND;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class RateLimitTest {
    @Test
    public void shouldNotBeLimited() {
        RateLimit limit = RateLimit.builder()
                .limitStrategy(DEFAULT_RATE_LIMIT_STRATEGY)
                .requestLimit(10)
                .durationMs(10 * MILLISECONDS_IN_SECOND)
                .build();
        limit.updateRate();

        assertThat(limit.isRateLimited(), is(false));
    }

    @Test
    public void shouldBeLimitedByNumberOfRequests() {
        RateLimit limit = RateLimit.builder()
                .limitStrategy(DEFAULT_RATE_LIMIT_STRATEGY)
                .requestLimit(2)
                .durationMs(10 * MILLISECONDS_IN_SECOND)
                .build();

        limit.updateRate();
        limit.updateRate();
        assertThat(limit.isRateLimited(), is(false));

        limit.updateRate();
        assertThat(limit.isRateLimited(), is(true));
    }

    @Test
    public void shouldBeLimitedByRequestsThenNotLimitedAfterShortWindowReset() throws Exception {
        RateLimit limit = RateLimit.builder()
                .limitStrategy(DEFAULT_RATE_LIMIT_STRATEGY)
                .requestLimit(2)
                .durationMs(1 * MILLISECONDS_IN_SECOND)
                .build();

        limit.updateRate();
        limit.updateRate();
        limit.updateRate();
        assertThat(limit.isRateLimited(), is(true));

        sleep(2 * MILLISECONDS_IN_SECOND);
        assertThat(limit.isRateLimited(), is(false));

    }

}