package au.com.unsol.rateLimiter;

/**
 * RateLimitStore interface for storing and retrieving RateLimit instances. The
 * purpose of this is to ensure state can be persisted between requests in order to
 * apply rate tests.
 * <p>
 * Should ideally be threadsafe as well as centralised if using clustered or
 * loadbalanced deployments.
 */
public interface RateLimitStore {
    /**
     * @param requesterKey
     * @return RateLimit for given requester key. New RateLimit if not found
     */
    RateLimit getData(String requesterKey);

    /**
     * Update the store with given RateLimit data
     *
     * @param requesterKey
     * @param rateData
     * @return
     */
    RateLimit putData(String requesterKey, RateLimit rateData);

    /**
     * Remove RateLimits from store that are older than given age
     *
     * @param ageMs
     */
    void trimRateLimits(long ageMs);

    /**
     * Current size of the store
     *
     * @return
     */
    long getNumberOfRateLimits();
}
