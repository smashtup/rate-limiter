package au.com.unsol.rateLimiter;

import org.junit.Test;

import static au.com.unsol.rateLimiter.RateLimitConfig.MILLISECONDS_IN_SECOND;
import static au.com.unsol.rateLimiter.RateLimitConfig.SECONDS_IN_MINUTE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class InMemoryRateLimitStoreTest {

    @Test
    public void shouldTrimRateLimitsOlderThanGivenAge() {
        InMemoryRateLimitStore memoryStore = new InMemoryRateLimitStore();
        long now = System.currentTimeMillis();
        RateLimit limitUser1 = RateLimit.builder().createdTimeMs(now - 5 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND).build();
        RateLimit limitUser2 = RateLimit.builder().createdTimeMs(now - 10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND).build();
        RateLimit limitUser3 = RateLimit.builder().createdTimeMs(now - 15 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND).build();
        memoryStore.putData("USER1", limitUser1);
        memoryStore.putData("USER2", limitUser2);
        memoryStore.putData("USER3", limitUser3);

        memoryStore.trimRateLimits(10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);
        assertThat(memoryStore.getNumberOfRateLimits(), is(1L));
        assertThat(memoryStore.getData("USER1"), notNullValue());
    }
}