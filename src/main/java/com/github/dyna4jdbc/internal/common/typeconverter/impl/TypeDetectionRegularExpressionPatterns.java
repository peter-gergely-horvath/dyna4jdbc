/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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

 
package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.regex.Pattern;

final class TypeDetectionRegularExpressionPatterns {
    
    private TypeDetectionRegularExpressionPatterns() {
        throw new AssertionError(TypeDetectionRegularExpressionPatterns.class + " is a static constants class!");
    }
    
    static final Pattern ONE_BIT = Pattern.compile("^([01])$");
    
    static final Pattern NUMBER_INTEGER = Pattern.compile("^[+-]?(\\d+)$");
    static final Pattern NUMBER_DECIMAL = Pattern.compile("^([+-])?(\\d+)(?:\\.(?<precision>\\d+))?$");

    static final Pattern TIMESTAMP = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?");

}
