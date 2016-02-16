/*
 * Copyright (C) 2013 Cumulocity GmbH
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

package c8y.trackeragent.protocol.gl200.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.Configuration;
import c8y.Restart;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.Translator;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * Try to parse a report and create a device on it anyway, even if the remainder
 * wasn't understood. Also, execute a verbatim command that was sent through the
 * configuration widget.
 */
@Component
public class GL200Fallback extends GL200Parser implements Translator {

    private final TrackerAgent trackerAgent;
    private final String password;

    @Autowired
    public GL200Fallback(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        this.password = PASSWORD;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        return true;
    }

    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        Configuration cfg = operation.get(Configuration.class);

        if (cfg != null) {
            operation.setStatus(OperationStatus.SUCCESSFUL.toString());
            return cfg.getConfig();
        }

        if (operation.get(Restart.class) != null) {
            return String.format("AT+GTRTO=%s,3,,,,,,0001$", password);
        }

        return null;
    }
}
