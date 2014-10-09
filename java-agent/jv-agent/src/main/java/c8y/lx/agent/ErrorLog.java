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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A cumulative error log that can be passed to the platform in alarms or in operation results.
 */
public class ErrorLog {

    private boolean empty = true;

    private StringWriter sw = new StringWriter();

    private PrintWriter pw = new PrintWriter(sw, true);

    public void add(String s) {
        empty = false;
        pw.println(s);
    }

    public void add(Throwable throwable) {
        empty = false;
        throwable.printStackTrace(pw);
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public String toString() {
        return sw.getBuffer().toString();
    }

    public static String toString(Throwable throwable) {
        ErrorLog el = new ErrorLog();
        el.add(throwable);
        return el.toString();
    }
}
