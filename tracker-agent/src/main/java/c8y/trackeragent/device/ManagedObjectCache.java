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

package c8y.trackeragent.device;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cumulocity.model.idtype.GId;

public class ManagedObjectCache extends ConcurrentHashMap<GId, TrackerDevice> {

    private static final long serialVersionUID = 1L;

    private static ManagedObjectCache instance = new ManagedObjectCache();
    private ConcurrentMap<GId, TrackerDevice> deviceByGid = new ConcurrentHashMap<GId, TrackerDevice>();
    private ConcurrentMap<String, TrackerDevice> deviceByImei = new ConcurrentHashMap<String, TrackerDevice>();

    public static ManagedObjectCache instance() {
        return instance;
    }

    public void put(TrackerDevice device) {
        deviceByGid.put(device.getGId(), device);
        deviceByImei.put(device.getImei(), device);
    }

    public void evict(String imei) {
        if (imei != null) {
            TrackerDevice device = get(imei);
            deviceByImei.remove(imei);
            if (device != null && device.getGId() != null) {
                deviceByGid.remove(device.getGId());
            }
        }
    }

    public TrackerDevice get(String imei) {
        return deviceByImei.get(imei);
    }

    public TrackerDevice get(GId gid) {
        return deviceByGid.get(gid);
    }
}
