package au.com.unsol.rateLimiter;

import org.junit.Test;

import static au.com.unsol.rateLimiter.RateLimitConfig.MILLISECONDS_IN_SECOND;
import static au.com.unsol.rateLimiter.RateLimitConfig.SECONDS_IN_MINUTE;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class RateLimitHandlerTest {

    @Test
    public void shouldUpdateConfigForRateLimitControllerForAllNewlyCreatedRateLimits() throws Exception {
        RateLimitStore rateLimitStore = new InMemoryRateLimitStore();
        RateLimitStrategy rateLimitStrategy = new FixedWindowStrategy();
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .rateLimitStore(rateLimitStore)
                .limitStrategy(rateLimitStrategy)
                .requestLimit(10000)
                .durationMs(20000)
                .ageToTrimMs(30000)
                .trimTimeIntervalMs(40000)
                .build();

        RateLimitHandler rateLimitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);

        RateLimit user1 = rateLimitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER1");
        assertThat(user1.getLimitStrategy(), is(rateLimitStrategy));
        assertThat(user1.getRequestLimit(), is(10000L));
        assertThat(user1.getDurationMs(), is(20000L));
        assertThat(rateLimitHandler.currentConfig().getRateLimitStore(), is(rateLimitStore));
        assertThat(rateLimitHandler.currentConfig().getAgeToTrimMs(), is(30000L));
        assertThat(rateLimitHandler.currentConfig().getTrimTimeIntervalMs(), is(40000L));

        RateLimitStore rateLimitStore2 = new InMemoryRateLimitStore();
        RateLimitStrategy rateLimitStrategy2 = new FixedWindowStrategy();
        RateLimitConfig rateLimitConfig2 = RateLimitConfig.builder()
                .rateLimitStore(rateLimitStore2)
                .limitStrategy(rateLimitStrategy2)
                .requestLimit(100)
                .durationMs(200)
                .ageToTrimMs(300)
                .trimTimeIntervalMs(400)
                .build();

        rateLimitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig2);

        RateLimit user2 = rateLimitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER2");
        assertThat(user2.getLimitStrategy(), is(rateLimitStrategy2));
        assertThat(user2.getRequestLimit(), is(100L));
        assertThat(user2.getDurationMs(), is(200L));
        assertThat(rateLimitHandler.currentConfig().getRateLimitStore(), is(rateLimitStore2));
        assertThat(rateLimitHandler.currentConfig().getAgeToTrimMs(), is(300L));
        assertThat(rateLimitHandler.currentConfig().getTrimTimeIntervalMs(), is(400L));
    }

    @Test(expected = InvalidRateLimitConfigException.class)
    public void shouldThrowExceptionWhenUpdatedWithNullConfig() throws Exception {
        RateLimitHandler rateLimitHandler = RateLimitHandler.getInstance().updateConfig(null);
    }

    @Test
    public void shouldGiveRateLimitObjectForGivenKey() {
        RateLimitHandler limitHandler = RateLimitHandler.getInstance();
        RateLimit limit = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER");
        assertThat(limit, notNullValue());
        assertThat(limit.isRateLimited(), is(false));
    }

    @Test
    public void shouldGiveRateLimitObjectForGivenKeyThatIsLimited() throws Exception {
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .requestLimit(1)
                .build();
        RateLimitHandler limitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);

        limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER");
        RateLimit limit = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER");
        assertThat(limit.isRateLimited(), is(true));
    }

    @Test
    public void shouldInvokeTrimDataStore() throws Exception {
        RateLimitStore rateLimitStore = new InMemoryRateLimitStore();
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .rateLimitStore(rateLimitStore)
                .trimTimeIntervalMs(MILLISECONDS_IN_SECOND)
                .ageToTrimMs(10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND)
                .build();
        RateLimitHandler limitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);

        RateLimit user1 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER1");

        RateLimit user2 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER2");
        user2.setCreatedTimeMs(user1.getCreatedTimeMs() - 10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);

        RateLimit user3 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER3");
        user3.setCreatedTimeMs(user1.getCreatedTimeMs() - 15 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);

        assertThat(rateLimitStore.getNumberOfRateLimits(), is(3L));

        sleep(MILLISECONDS_IN_SECOND);

        limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER1");
        assertThat(rateLimitStore.getNumberOfRateLimits(), is(1L));
        assertThat(rateLimitStore.getData("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER1"), notNullValue());
    }

    @Test
    public void shouldNotInvokeTrimDataStore() throws Exception {
        RateLimitStore rateLimitStore = new InMemoryRateLimitStore();
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .rateLimitStore(rateLimitStore)
                .trimTimeIntervalMs(10 * MILLISECONDS_IN_SECOND)
                .ageToTrimMs(10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND)
                .build();
        RateLimitHandler limitHandler = RateLimitHandler.getInstance().updateConfig(rateLimitConfig);

        RateLimit user1 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER1");

        RateLimit user2 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER2");
        user2.setCreatedTimeMs(user1.getCreatedTimeMs() - 10 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);

        RateLimit user3 = limitHandler.registerRequest("THIS_SHOULD_UNIQUELY_IDENTIFY_THE_REQUESTER3");
        user3.setCreatedTimeMs(user1.getCreatedTimeMs() - 15 * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND);

        sleep(MILLISECONDS_IN_SECOND);

        assertThat(rateLimitStore.getNumberOfRateLimits(), is(3L));
    }
}