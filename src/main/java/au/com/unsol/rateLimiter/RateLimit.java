package au.com.unsol.rateLimiter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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

    public boolean isRateLimited() {
        return limitStrategy.isRateLimited(rateData, requestLimit, durationMs);
    }

    public void updateRate() {
        rateData = limitStrategy.updateRate(rateData, requestLimit, durationMs);
    }
}
