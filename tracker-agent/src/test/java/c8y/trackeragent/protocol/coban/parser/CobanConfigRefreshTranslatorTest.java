/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.coban.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

import c8y.Command;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.protocol.coban.device.CobanDevice;
import c8y.trackeragent.utils.message.TrackerMessage;

@RunWith(MockitoJUnitRunner.class)
public class CobanConfigRefreshTranslatorTest extends CobanParserTestSupport {

    private CobanConfigRefreshTranslator bean;

    @Before
    public void init() {
        bean = new CobanConfigRefreshTranslator(trackerAgent, serverMessages);
        connectionDetails.setImei("123123");
    }

    @Test
    public void shouldHandleRefreshConfigOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(new Object(), CobanConfigRefreshTranslator.OPERATION_MARKER);
        OperationContext operationCtx = new OperationContext(connectionDetails, operation);
        currentCobanDeviceIs(new CobanDevice().setLocationReportInterval("30s"));

        String serverMessageText = bean.translate(operationCtx);

        TrackerMessage actual = serverMessages.msg(serverMessageText);
        TrackerMessage expected = serverMessages.timeIntervalLocationRequest("123123", "30s");
        assertThat(actual).isEqualTo(expected);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.SUCCESSFUL.toString());
        assertThat(operation.get(CobanSupport.OPERATION_FRAGMENT_SERVER_COMMAND)).isEqualTo(expected.asText());

    }

    @Test
    public void shouldHandleCommandOperation() throws Exception {
        OperationRepresentation operation = new OperationRepresentation();
        operation.set(new Command("CMD"));
        OperationContext operationCtx = new OperationContext(connectionDetails, operation);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if (args != null && args.length > 0 && args[0] instanceof OperationRepresentation) {
                    OperationRepresentation operation = (OperationRepresentation) args[0];
                    operation.setStatus(OperationStatus.SUCCESSFUL.toString());
                }
                return null;
            }
        }).when(deviceMock).setOperationSuccessful(any(OperationRepresentation.class));

        String textForwardedToDevice = bean.translate(operationCtx);
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.SUCCESSFUL.toString());
        assertThat(textForwardedToDevice).isEqualTo("CMD");
    }

}
