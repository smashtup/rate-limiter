package au.com.unsol.rateLimiter;

import java.util.HashMap;
import java.util.Map;

public class FixedWindowStrategy implements RateLimitStrategy {

    public static final String NUMBER_OF_REQUESTS_KEY = "numberOfRequests";
    public static final String TIME_OF_FIRST_REQUEST_MS_KEY = "timeOfFirstRequestMs";

    @Override
    public boolean isRateLimited(Map<String, String> requestData, long requestLimit, long durationMs) {
        return !hasBucketReset(requestData, System.currentTimeMillis(), durationMs) && requestLimit <= getNumberOfRequests(requestData);
    }

    @Override
    public Map<String, String> updateRate(Map<String, String> requestData, long requestLimit, long durationMs) {
        long now = System.currentTimeMillis();
        Map<String, String> ratedData = new HashMap<>();
        if (null != requestData) {
            ratedData.putAll(requestData);
        }

        if (ratedData.isEmpty() || hasBucketReset(ratedData, now, durationMs)) {
            setNumberOfRequests(ratedData, 0);
            setTimeOfFirstRequestMs(ratedData, now);
        } else {
            setNumberOfRequests(ratedData, getNumberOfRequests(ratedData) + 1);
        }

        return ratedData;
    }

    private long getNumberOfRequests(Map<String, String> requestData) {
        return Long.valueOf(requestData.get(NUMBER_OF_REQUESTS_KEY));
    }

    private void setNumberOfRequests(Map<String, String> requestData, long numberOfRequests) {
        requestData.put(NUMBER_OF_REQUESTS_KEY, String.valueOf(numberOfRequests));
    }

    private long getTimeOfFirstRequestMs(Map<String, String> requestData) {
        return Long.valueOf(requestData.get(TIME_OF_FIRST_REQUEST_MS_KEY));
    }

    private void setTimeOfFirstRequestMs(Map<String, String> requestData, long timeInMs) {
        requestData.put(TIME_OF_FIRST_REQUEST_MS_KEY, String.valueOf(timeInMs));
    }

    private long getResetTimeMs(Map<String, String> requestData, long durationMs) {
        return getTimeOfFirstRequestMs(requestData) + durationMs;
    }


    private boolean hasBucketReset(Map<String, String> requestData, long currentTimeInMillis, long durationMs) {
        return currentTimeInMillis >= getResetTimeMs(requestData, durationMs);
    }

}
