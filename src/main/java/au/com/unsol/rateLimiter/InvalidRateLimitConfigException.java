package au.com.unsol.rateLimiter;

public class InvalidRateLimitConfigException extends Exception {
    InvalidRateLimitConfigException(String message) {
        super(message);
    }
}
