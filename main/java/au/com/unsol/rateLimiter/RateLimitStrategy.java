package au.com.unsol.rateLimiter;

import java.util.Map;

public interface RateLimitStrategy {
    boolean isRateLimited(Map<String, String> requestData, long requestLimit, long durationMs);

    Map<String, String> updateRate(Map<String, String> requestData, long requestLimit, long durationMs);
}