package au.com.unsol.rateLimiter;

/**
 * InvalidationRateLimitConfigException - Exception thrown when config used by the RateLimitHandler
 * is invalid.
 */
public class InvalidRateLimitConfigException extends Exception {
    InvalidRateLimitConfigException(String message) {
        super(message);
    }
}
