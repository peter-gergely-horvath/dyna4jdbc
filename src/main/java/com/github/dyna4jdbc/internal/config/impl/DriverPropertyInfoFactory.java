/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.config.impl;

import java.sql.DriverPropertyInfo;
import java.util.Properties;

public final class DriverPropertyInfoFactory {

    private DriverPropertyInfoFactory() {
        // no instances allowed
    }

    public static DriverPropertyInfo[] getDriverPropertyInfo(String url, Properties info) {
        ConfigurationEntry[] input = ConfigurationEntry.values();
        DriverPropertyInfo[] result = new DriverPropertyInfo[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].getDriverPropertyInfo();
        }
        return result;
    }
}

