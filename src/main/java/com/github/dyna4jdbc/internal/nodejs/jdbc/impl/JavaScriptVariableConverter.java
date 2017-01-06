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

 
package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class JavaScriptVariableConverter {
    
    private JavaScriptVariableConverter() {
        throw new AssertionError(JavaScriptVariableConverter.class + " is a static utility class!");
    }

    static String convertToString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof java.lang.String || value instanceof java.lang.Character) {
            return String.format("'%s'", ((String) value).replace("[^\\]'", "\\'"));
        } else if (value instanceof Integer || value instanceof java.lang.Boolean
                || value instanceof java.lang.Byte || value instanceof java.lang.Short
                || value instanceof java.lang.Integer || value instanceof java.lang.Long
                || value instanceof java.lang.Float || value instanceof java.lang.Double) {
           return value.toString();
        } else if (value instanceof Date) {
            long time = ((Date) value).getTime();
            return String.format("new Date(%s)", time);
        } else if (value instanceof Collection) {
            List<String> transformed = ((Collection<?>) value).stream()
                .map(JavaScriptVariableConverter::convertToString)
                .collect(Collectors.toList());
            return String.format("[ %s ]", String.join(", ", transformed));
        } else if (value.getClass().isArray()) {
            List<String> transformed = Arrays.asList((Object[]) value).stream()
                    .map(JavaScriptVariableConverter::convertToString)
                    .collect(Collectors.toList());
            return String.format("[ %s ]", String.join(", ", transformed));
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;
    
            List<String> mapEntriesAsString = map.entrySet().stream()
                .map(entry -> {
                            String key = convertToString(entry.getKey());
                            String valueString = convertToString(entry.getValue());
    
                            return String.format("%s = %s;", key, valueString);
                })
                .collect(Collectors.toList());
            return String.format("{ %s }", String.join(", ", mapEntriesAsString));
        } else {
            return convertToString(value.toString());
        }
    }

}
