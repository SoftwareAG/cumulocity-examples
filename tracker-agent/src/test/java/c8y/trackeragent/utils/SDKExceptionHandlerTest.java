/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

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
