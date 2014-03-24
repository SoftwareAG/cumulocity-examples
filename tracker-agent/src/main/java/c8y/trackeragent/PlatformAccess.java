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

package c8y.trackeragent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;

/**
 * This class reads credentials from a configuration file and provides a link to
 * the platform.
 */
public class PlatformAccess {
    
    public static final String PROPS = "/cumulocity.properties";
    
    private Platform platform;
    private Properties props;

    public PlatformAccess() throws IOException {
        props = new Properties();
        try (InputStream is = getClass().getResourceAsStream(PROPS); InputStreamReader ir = new InputStreamReader(is)) {
            props.load(ir);
        }

        String host = props.getProperty("host", "http://developer.cumulocity.com");
        String user = props.getProperty("user");
        String password = props.getProperty("password");

        platform = new PlatformImpl(host, new CumulocityCredentials(user, password));

    }

    public Platform getPlatform() {
        return platform;
    }

    public Properties getProps() {
        return props;
    }
}
