package c8y.c8y.trackeragent.operations;

import com.cumulocity.sdk.client.SDKException;
import com.google.common.base.Optional;
import org.junit.Test;

import static c8y.trackeragent.operations.OperationDispatcher.findCause;
import static org.fest.assertions.Assertions.assertThat;

public class OperationDispatcherTest {
    @Test
    public void findCauseShouldWorkForCorrectCause() {
        final SDKException cause = new SDKException("Cause");

        final Optional<SDKException> found = findCause(SDKException.class, new RuntimeException(cause));

        assertThat(found.get()).isSameAs(cause);
    }

    @Test
    public void findCauseShouldWorkForNonExistingCause() {
        final NullPointerException cause = new NullPointerException("Cause");

        final Optional<SDKException> found = findCause(SDKException.class, new RuntimeException(cause));

        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void findCauseShouldWorkForNull() {
        final Optional<SDKException> found = findCause(SDKException.class, null);

        assertThat(found.isPresent()).isFalse();
    }
}
