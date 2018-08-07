package au.com.unsol.rateLimiter;

import java.util.Map;

/**
 * RateLimitStrategy - Use this interface to implement different rate limiting strategy than
 * those currently provided by the library.
 */
public interface RateLimitStrategy {
    /**
     * Test whether the given request data is limited or not for the given requestLimit
     * and duration
     *
     * @param requestData  Data required for rate limit test
     * @param requestLimit Max number of requests
     * @param durationMs   Time interval in milliseconds for this strategy
     * @return true if limiting should be applied
     */
    boolean isRateLimited(Map<String, String> requestData, long requestLimit, long durationMs);

    /**
     * Update given data with information required by the strategy to perform rate limit test
     *
     * @param requestData  Data required for rate limit test
     * @param requestLimit Max number of requests
     * @param durationMs   Time interval in milliseconds for this strategy
     * @return new copy of updated request data
     */
    Map<String, String> updateRate(Map<String, String> requestData, long requestLimit, long durationMs);
}