package au.com.unsol.rateLimiter;

public interface RateLimitStore {
    RateLimit getData(String requesterKey);

    RateLimit putData(String requesterKey, RateLimit rateData);

    void trimRateLimits(long ageMs);

    long getNumberOfRateLimits();
}
