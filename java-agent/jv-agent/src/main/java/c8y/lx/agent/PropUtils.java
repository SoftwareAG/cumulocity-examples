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


package c8y.lx.agent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropUtils.class);

    public static Properties fromString(String propsStr) throws IOException {
        Properties result = new Properties();
        try (StringReader reader = new StringReader(propsStr)) {
            result.load(reader);
            logger.debug("Read configuration: " + result);
        }
        return result;
    }

    public static String toString(Properties props) {
        String result = "";
        try (StringWriter configWriter = new StringWriter()) {
            props.store(configWriter, null);
            result = configWriter.getBuffer().toString();
        } catch (IOException iox) {
            // Storing in a String shouldn't cause I/O exception
            logger.warn("Bogus IOException", iox);
        }
        return result;
    }

    public static Properties fromFile(String file) {
        Properties props = new Properties();
        fromFile(file, props);
        return props;
    }

    public static void fromFile(String file, Properties props) {
        try (FileReader reader = new FileReader(file)) {
            props.load(reader);
            logger.debug("Read configuration file, current configuration: " + props);
        } catch (IOException iox) {
            logger.warn("Configuration file {} cannot be read, assuming empty configuration", file);
        }
    }

    public static String toFile(Properties props, String file) {
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, null);
        } catch (IOException iox) {
            logger.warn("Configuration file {} cannot be written", file);
            return ErrorLog.toString(iox);
        }
        return null;
    }
}
