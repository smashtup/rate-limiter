package au.com.unsol.rateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryRateLimitStore - A simple HashMap implementation of the RateLimitStore.
 * ConcurrentHashMap has been used to provide thread safety
 */
public class InMemoryRateLimitStore implements RateLimitStore {

    private Map<String, RateLimit> rateLimiterDataStore;


    public InMemoryRateLimitStore() {
        rateLimiterDataStore = new ConcurrentHashMap<>();
    }

    @Override
    public RateLimit getData(String requesterKey) {
        RateLimit rateLimit = rateLimiterDataStore.get(requesterKey);
        return null != rateLimit ? rateLimit.toBuilder().build() : null;
    }

    @Override
    public RateLimit putData(String requesterKey, RateLimit rateData) {
        return rateLimiterDataStore.put(requesterKey, rateData);
    }

    @Override
    public void trimRateLimits(long ageMs) {
        long now = System.currentTimeMillis();
        rateLimiterDataStore.entrySet().stream()
                .filter(entry -> (now - entry.getValue().getCreatedTimeMs()) >= ageMs)
                .forEach(entry -> rateLimiterDataStore.remove(entry.getKey()));
    }

    @Override
    public long getNumberOfRateLimits() {
        return rateLimiterDataStore.keySet().size();
    }
}