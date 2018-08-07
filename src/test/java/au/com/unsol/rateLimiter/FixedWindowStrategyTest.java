package au.com.unsol.rateLimiter;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static au.com.unsol.rateLimiter.FixedWindowStrategy.NUMBER_OF_REQUESTS_KEY;
import static au.com.unsol.rateLimiter.FixedWindowStrategy.TIME_OF_FIRST_REQUEST_MS_KEY;
import static au.com.unsol.rateLimiter.RateLimitConfig.MILLISECONDS_IN_SECOND;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class FixedWindowStrategyTest {

    @Test
    public void shouldCreateNewRateDataWhenDataNotGiven() {
        FixedWindowStrategy fixedWindowStrategy = new FixedWindowStrategy();

        Map<String, String> rateData = fixedWindowStrategy.updateRate(null, 1000, 60 * 60 * MILLISECONDS_IN_SECOND);

        assertThat(rateData, notNullValue());
        assertThat(rateData.keySet(), hasItems(NUMBER_OF_REQUESTS_KEY, TIME_OF_FIRST_REQUEST_MS_KEY));
    }

    @Test
    public void shouldIncrementRequestForNonExpiredWindowWithNewMapObject() {
        FixedWindowStrategy fixedWindowStrategy = new FixedWindowStrategy();
        Map<String, String> rateData = fixedWindowStrategy.updateRate(null, 1000, 60 * 60 * MILLISECONDS_IN_SECOND);
        assertThat(rateData.get(NUMBER_OF_REQUESTS_KEY), is("0"));

        Map<String, String> newRateData = fixedWindowStrategy.updateRate(rateData, 1000, 60 * 60 * MILLISECONDS_IN_SECOND);
        // Ensure original map not modified
        assertThat(rateData.get(NUMBER_OF_REQUESTS_KEY), is("0"));
        // Ensure new map incremented
        assertThat(newRateData.get(NUMBER_OF_REQUESTS_KEY), is("1"));
    }

    @Test
    public void shouldReturnTrueForRateLimitedOnMaxNumberOfRequests() {
        FixedWindowStrategy fixedWindowStrategy = new FixedWindowStrategy();
        Map<String, String> rateData = new HashMap<>();
        rateData.put(NUMBER_OF_REQUESTS_KEY, "1000");
        rateData.put(TIME_OF_FIRST_REQUEST_MS_KEY, String.valueOf(System.currentTimeMillis()));

        assertThat(fixedWindowStrategy.isRateLimited(rateData, 1000, 60 * 60 * MILLISECONDS_IN_SECOND), is(true));
    }
}