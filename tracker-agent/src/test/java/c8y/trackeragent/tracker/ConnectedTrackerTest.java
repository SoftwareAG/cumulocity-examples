/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent.tracker;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static c8y.trackeragent.utils.Devices.IMEI_1;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.devicebootstrap.DeviceBootstrapProcessor;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.TestConnectionDetails;
import c8y.trackeragent.service.TrackerDeviceContextService;

public class ConnectedTrackerTest {
    
    private TrackerDeviceContextService contextService = mock(TrackerDeviceContextService.class);
    private Translator translator = mock(Translator.class);
    private Parser parser = mock(Parser.class);
    private DeviceBootstrapProcessor bootstrapProcessor = mock(DeviceBootstrapProcessor.class);
    private DeviceCredentialsRepository credentialsRepository = mock(DeviceCredentialsRepository.class);
    private ConnectedTracker tracker;
    private TestConnectionDetails connectionDetails = new TestConnectionDetails(); 
    private String[] dummyReport = new String[] { "dummyReport" };

    @Before
    public void setup() throws Exception {
        // @formatter:off
        tracker = new BaseConnectedTracker<Fragment>(
        		asList(translator, parser),
        		bootstrapProcessor,
        		credentialsRepository,
        		contextService) {

                    @Override
                    public TrackingProtocol getTrackingProtocol() {
                        return TestConnectionDetails.DEFAULT_PROTOCOL;
                    }
            
        };
        // @formatter:on
    }

    @Test
    public void shouldProcessReportSucessfully() throws Exception {
    	when(credentialsRepository.getDeviceCredentials(IMEI_1)).thenReturn(DeviceCredentials.forDevice(IMEI_1, "tenant"));
    	when(credentialsRepository.getAgentCredentials("tenant")).thenReturn(DeviceCredentials.forAgent("tenant", "user", "password"));
        when(parser.parse(dummyReport)).thenReturn(IMEI_1);
        when(parser.onParsed(any(ReportContext.class))).thenReturn(true);

        tracker.executeReports(connectionDetails, getBytes("dummyReport"));

        verify(parser).parse(dummyReport);
        verifyZeroInteractions(translator);
        assertThat(connectionDetails.getImei()).isEqualTo(IMEI_1);
    }
    
    @Test
    public void singleIgnoreReportForUnknownImei() throws Exception {
    	when(credentialsRepository.getDeviceCredentials(IMEI_1)).thenThrow(UnknownDeviceException.forImei(IMEI_1));
    	when(credentialsRepository.getAgentCredentials("tenant")).thenThrow(UnknownTenantException.forTenantId("tenant"));
        when(parser.parse(dummyReport)).thenReturn(IMEI_1);
        when(parser.onParsed(any(ReportContext.class))).thenReturn(false);
        
        tracker.executeReports(connectionDetails, getBytes("dummyReport"));
        
        assertThat(connectionDetails.getImei()).isNull();
        verify(bootstrapProcessor).tryAccessDeviceCredentials(IMEI_1);
        verifyZeroInteractions(translator);
    }

    @Test
    public void operationExecution() throws Exception {
    	when(credentialsRepository.getDeviceCredentials(IMEI_1)).thenThrow(UnknownDeviceException.forImei(IMEI_1));
    	when(credentialsRepository.getAgentCredentials("tenant")).thenThrow(UnknownTenantException.forTenantId("tenant"));    	
        String translation = "translation";
        OperationContext operationContext = new OperationContext(connectionDetails, null);
        when(translator.translate(operationContext)).thenReturn(translation);

        tracker.executeOperation(operationContext);

        verifyZeroInteractions(parser);
        verify(translator).translate(operationContext);
        assertThat(connectionDetails.getOut()).isEqualTo(translation);
    }
}
