package au.com.unsol.rateLimiter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * RateLimit - Provides a container for any state used in rate limit calculations
 * including the strategy to be used.
 * <p>
 * Also provides calling application with means to test whether rate limit is in effect
 * or not
 */
@Builder(toBuilder = true)
public class RateLimit {
    @Getter
    private RateLimitStrategy limitStrategy;
    @Getter
    private long requestLimit;
    @Getter
    private long durationMs;
    @Getter
    @Setter
    @Builder.Default
    private long createdTimeMs = System.currentTimeMillis();
    @Builder.Default
    private Map<String, String> rateData = new HashMap<>();

    /**
     * Invoke strategy as to whether the current state is limited or not
     * and duration
     *
     * @return
     */
    public boolean isRateLimited() {
        return limitStrategy.isRateLimited(rateData, requestLimit, durationMs);
    }

    /**
     * Invoke strategy update rate data for a registered request
     */
    public void updateRate() {
        rateData = limitStrategy.updateRate(rateData, requestLimit, durationMs);
    }
}
