package au.com.unsol.rateLimiter;

import lombok.Synchronized;

public class RateLimitHandler {
    public static final RateLimitConfig DEFAULT_CONFIG = RateLimitConfig.builder().build();

    private static RateLimitHandler instance;

    private RateLimitConfig rateLimitConfig = DEFAULT_CONFIG;

    private RateLimitHandler() {
    }

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

    public RateLimitHandler updateConfig(RateLimitConfig rateLimitConfig) throws InvalidRateLimitConfigException {
        if (null == rateLimitConfig) {
            throw new InvalidRateLimitConfigException("Can't update RateLimitHandler with null config");
        }
        rateLimitConfig.validate();
        this.rateLimitConfig = rateLimitConfig;
        return this;

    }

    public RateLimitConfig currentConfig() {
        return this.rateLimitConfig;
    }

    public RateLimit registerRequest(String requesterKey) {
        return this.registerRequest(requesterKey, this.rateLimitConfig);
    }

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
