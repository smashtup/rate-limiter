package au.com.unsol.rateLimiter;

import org.junit.Test;

import static au.com.unsol.rateLimiter.RateLimitConfig.CANNOT_BE_NULL;
import static au.com.unsol.rateLimiter.RateLimitConfig.GREATER_THAN_OR_EQUAL_TO;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class RateLimitConfigTest {
    @Test(expected = InvalidRateLimitConfigException.class)
    public void shouldThrowExceptionAfterValidation() throws InvalidRateLimitConfigException {
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .rateLimitStore(null)
                .limitStrategy(null)
                .requestLimit(-1)
                .durationMs(-1)
                .ageToTrimMs(-1)
                .trimTimeIntervalMs(-1)
                .build();

        rateLimitConfig.validate();
    }

    @Test
    public void shouldProvideAMessageForAllInvalidFieldsInExceptionAfterValidation() {
        RateLimitConfig rateLimitConfig = RateLimitConfig.builder()
                .rateLimitStore(null)
                .limitStrategy(null)
                .requestLimit(-1)
                .durationMs(-1)
                .ageToTrimMs(-1)
                .trimTimeIntervalMs(-1)
                .build();

        try {
            rateLimitConfig.validate();
        } catch (InvalidRateLimitConfigException e) {
            assertThat(e.getMessage(), containsString(format(CANNOT_BE_NULL, "rateLimitStore")));
            assertThat(e.getMessage(), containsString(format(CANNOT_BE_NULL, "rateLimitStrategy")));
            assertThat(e.getMessage(), containsString(format(GREATER_THAN_OR_EQUAL_TO, "requestLimit", 0)));
            assertThat(e.getMessage(), containsString(format(GREATER_THAN_OR_EQUAL_TO, "durationMs", 0)));
            assertThat(e.getMessage(), containsString(format(GREATER_THAN_OR_EQUAL_TO, "ageToTrimMs", 0)));
            assertThat(e.getMessage(), containsString(format(GREATER_THAN_OR_EQUAL_TO, "trimTimeInterval", 0)));
        }

    }

}