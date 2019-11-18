package c8y.trackeragent.utils;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Optional;
import org.junit.Test;

import static c8y.trackeragent.utils.SDKExceptionHandler.findException;
import static org.assertj.core.api.Assertions.assertThat;

public class SDKExceptionHandlerTest {
    @Test
    public void findCauseShouldWorkForCorrectCause() {
        final SDKException cause = new SDKException("Cause");

        final Optional<SDKException> found = findException(SDKException.class, new RuntimeException(cause));

        assertThat(found.get()).isSameAs(cause);
    }

    @Test
    public void findCauseShouldWorkForNonExistingCause() {
        final NullPointerException cause = new NullPointerException("Cause");

        final Optional<SDKException> found = findException(SDKException.class, new RuntimeException(cause));

        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void findCauseShouldWorkForNull() {
        final Optional<SDKException> found = findException(SDKException.class, null);

        assertThat(found.isPresent()).isFalse();
    }
}
