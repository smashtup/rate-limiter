package au.com.unsol.rateLimiter;

import lombok.Synchronized;

/**
 * RateLimitHandler (For usage refer to https://github.com/smashtup/rate-limiter#example
 * <p>
 * A simple and rudimentary Java library to help provide an interface for rate
 * limiting to your java application. Interface can be extended with own rate
 * limiting strategies as well as storage for rate limiting state. Using the
 * In Memory storage limits this library to a single application server.
 * Please look into implementing a store based on a central storage such as
 * Redis for clustered support.
 * <p>
 * Supported Rate Limit Strategies:
 * <p>
 * -Fixed Window
 * <p>
 * Supported Rate Limit Data Storage:
 * <p>
 * - In Memory Concurrent Map
 */
public class RateLimitHandler {
    public static final RateLimitConfig DEFAULT_CONFIG = RateLimitConfig.builder().build();

    private static RateLimitHandler instance;

    private RateLimitConfig rateLimitConfig = DEFAULT_CONFIG;

    private RateLimitHandler() {
    }

    /**
     * Retrieve an instance of the Handler using default configuration
     *
     * @return a singleton instance of this class
     */
    public static RateLimitHandler getInstance() {
        if (null == instance) {
            synchronized (RateLimitHandler.class) {
                if (null == instance) {
                    instance = new RateLimitHandler();
                }
            }
        }
        return instance;
    }

    /**
     * Update the config of the handler for all new future RateLimit requests
     *
     * @param rateLimitConfig
     * @return a singleton instance of this class
     * @throws InvalidRateLimitConfigException
     */
    public RateLimitHandler updateConfig(RateLimitConfig rateLimitConfig) throws InvalidRateLimitConfigException {
        if (null == rateLimitConfig) {
            throw new InvalidRateLimitConfigException("Can't update RateLimitHandler with null config");
        }
        rateLimitConfig.validate();
        this.rateLimitConfig = rateLimitConfig;
        return this;

    }

    /**
     * @return the current configuration object of this handler
     */
    public RateLimitConfig currentConfig() {
        return this.rateLimitConfig;
    }

    /**
     * Register requests that you wish to test rate limiting on.
     *
     * @param requesterKey
     * @return The RateLimit object can be used for a rate limit test.
     */
    public RateLimit registerRequest(String requesterKey) {
        return this.registerRequest(requesterKey, this.rateLimitConfig);
    }

    /**
     * Register requests that you wish to test rate limiting on using
     * a specific rate limit config. Only called directly for mainly testing.
     * To ensure config is persisted for all registerRequest use updateConfig
     *
     * @param requesterKey
     * @param rateLimitConfig
     * @return The RateLimit object can be used for a rate limit test.
     */
    @Synchronized
    public RateLimit registerRequest(String requesterKey, RateLimitConfig rateLimitConfig) {
        trimRateLimitStore();
        RateLimit rateData = rateLimitConfig.getRateLimitStore().getData(requesterKey);
        if (null == rateData) {
            rateData = RateLimit.builder()
                    .limitStrategy(rateLimitConfig.getLimitStrategy())
                    .requestLimit(rateLimitConfig.getRequestLimit())
                    .durationMs(rateLimitConfig.getDurationMs())
                    .build();
        }
        rateData.updateRate();
        rateLimitConfig.getRateLimitStore().putData(requesterKey, rateData);
        return rateData;
    }

    private void trimRateLimitStore() {
        long now = System.currentTimeMillis();
        if ((now - rateLimitConfig.getLastTrimTimeMs()) >= rateLimitConfig.getTrimTimeIntervalMs()) {
            rateLimitConfig.setLastTrimTimeMs(now);
            rateLimitConfig.getRateLimitStore().trimRateLimits(rateLimitConfig.getAgeToTrimMs());
        }
    }
}
