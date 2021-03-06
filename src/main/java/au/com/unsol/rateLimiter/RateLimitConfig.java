package au.com.unsol.rateLimiter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.String.format;

/**
 * RateLimitConfig - Used to configure the handler with rate limiter parameters.
 * <p>
 * Preferably use the builder() to build objects of this class.
 * <p>
 * All time measurements are in milliseconds
 */
@Builder
@Getter
public class RateLimitConfig {
    public static final long MILLISECONDS_IN_SECOND = 1000;
    public static final long SECONDS_IN_MINUTE = 60;
    public static final long MINUTES_IN_HOUR = 60;
    public static final long HOURS_IN_DAY = 24;

    public static final String CANNOT_BE_NULL = "%s can't be null";
    public static final String GREATER_THAN_OR_EQUAL_TO = "%s must be greater than or equal to %d";

    public static final RateLimitStrategy DEFAULT_RATE_LIMIT_STRATEGY = new FixedWindowStrategy();
    public static final long DEFAULT_REQUEST_LIMIT = 100;
    public static final long DEFAULT_DURATION_IN_MS = 1 * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    public static final long DEFAULT_TRIM_TIME_INTERVAL_MS = 10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    public static final long DEFAULT_AGE_TO_TRIM_MS = 3 * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;

    /**
     * Override this with your own implementation of RateLimitStore
     */
    @Builder.Default
    private RateLimitStore rateLimitStore = new InMemoryRateLimitStore();

    /**
     * Override this with your own implementation of RateLimitStrategy
     */
    @Builder.Default
    private RateLimitStrategy limitStrategy = DEFAULT_RATE_LIMIT_STRATEGY;

    /**
     * What is the max requests for the rate strategy you are using
     */
    @Builder.Default
    private long requestLimit = DEFAULT_REQUEST_LIMIT;

    /**
     * What is the interval in milliseconds for the rate strategy
     */
    @Builder.Default
    private long durationMs = DEFAULT_DURATION_IN_MS;

    /**
     * How often should RateLimit entries be cleaned up
     */
    @Builder.Default
    private long trimTimeIntervalMs = DEFAULT_TRIM_TIME_INTERVAL_MS;

    /**
     * What age should RateLimit entries be before being cleaned up
     */
    @Builder.Default
    private long ageToTrimMs = DEFAULT_AGE_TO_TRIM_MS;

    /**
     * Used by the trimmer
     */
    @Builder.Default
    @Setter
    private long lastTrimTimeMs = System.currentTimeMillis();

    /**
     * Ensure this Config has valid values
     *
     * @throws InvalidRateLimitConfigException
     */
    public void validate() throws InvalidRateLimitConfigException {
        StringBuilder errorMessage = new StringBuilder();
        if (null == rateLimitStore) {
            errorMessage.append("\\n").append(format(CANNOT_BE_NULL, "rateLimitStore"));
        }
        if (null == limitStrategy) {
            errorMessage.append("\\n").append(format(CANNOT_BE_NULL, "rateLimitStrategy"));
        }
        if (requestLimit < 0) {
            errorMessage.append("\\n").append(format(GREATER_THAN_OR_EQUAL_TO, "requestLimit", 0));
        }
        if (durationMs < 0) {
            errorMessage.append("\\n").append(format(GREATER_THAN_OR_EQUAL_TO, "durationMs", 0));
        }
        if (trimTimeIntervalMs < 0) {
            errorMessage.append("\\n").append(format(GREATER_THAN_OR_EQUAL_TO, "trimTimeInterval", 0));
        }
        if (ageToTrimMs < 0) {
            errorMessage.append("\\n").append(format(GREATER_THAN_OR_EQUAL_TO, "ageToTrimMs", 0));
        }

        if (errorMessage.length() > 0) {
            throw new InvalidRateLimitConfigException(errorMessage.toString());
        }

    }

}