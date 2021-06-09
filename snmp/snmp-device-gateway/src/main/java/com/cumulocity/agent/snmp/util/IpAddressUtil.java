/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.util;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.UnsignedBytes;

import java.net.InetAddress;

public class IpAddressUtil {
    public static InetAddress forString(String ipAddress) throws IllegalArgumentException {
        return forString(ipAddress, false);
    }

    public static InetAddress forString(String ipAddress, boolean defaultToLoopbackAddress) throws IllegalArgumentException {
        if(ipAddress == null || ipAddress.trim().isEmpty()) {
            if(defaultToLoopbackAddress) {
                return InetAddress.getLoopbackAddress();
            }
            else {
                return null;
            }
        }

        return InetAddresses.forString(ipAddress.trim().split("[/%]", 2)[0].toLowerCase());
    }

    public static String sanitizeIpAddress(String ipAddress) throws IllegalArgumentException {
        return sanitizeIpAddress(ipAddress, false);
    }

    public static String sanitizeIpAddress(String ipAddress, boolean defaultToLoopbackAddress) throws IllegalArgumentException {
        InetAddress inetAddress = forString(ipAddress, defaultToLoopbackAddress);
        if(inetAddress == null) {
            return null;
        }

        return inetAddress.getHostAddress();
    }

    public static int compare(InetAddress firstAddress, InetAddress secondAddress) {
        return UnsignedBytes.lexicographicalComparator().compare(firstAddress.getAddress(), secondAddress.getAddress());
    }
}
