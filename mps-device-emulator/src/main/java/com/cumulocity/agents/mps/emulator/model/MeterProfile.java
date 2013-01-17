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

package com.cumulocity.agents.mps.emulator.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeterProfile {
    private boolean status;
    private List<MeterProfileResult> result = new ArrayList<MeterProfileResult>();
    private static final long THREE_HOURS_IN_MILLIS = 10800000;
    
    
    public static MeterProfile createSampleMeterProfile(Date start, Date end) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long difference = end.getTime() - start.getTime(); 
        MeterProfile profile = new MeterProfile();
        profile.setStatus(true);
        for (int i = 1; i < 10; i++) {
            if (difference > i*THREE_HOURS_IN_MILLIS) {
                profile.getResult().add(new MeterProfileResult(dateFormat.format(new Date(start.getTime() + (i*THREE_HOURS_IN_MILLIS))), 0.0));
            } else {
                break;
            }
        }
        return profile;
    }
    
    public MeterProfile() {}
    
    public MeterProfile(boolean status, List<MeterProfileResult> result) {
        this.status = status;
        this.result = result;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public List<MeterProfileResult> getResult() {
        return result;
    }
    public void setResult(List<MeterProfileResult> result) {
        this.result = result;
    }
}
