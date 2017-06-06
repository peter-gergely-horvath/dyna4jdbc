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

 
package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.util.*;
import java.util.stream.Collectors;

final class JavaScriptVariableConverter {
    
    private JavaScriptVariableConverter() {
        throw new AssertionError(JavaScriptVariableConverter.class + " is a static utility class!");
    }

    static String convertToString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String || value instanceof CharSequence) {
            return String.format("'%s'", value.toString().replaceAll("(?<!\\\\)[']", "\\\\'"));
        } else if (value instanceof Character) {
            return String.format("'%s'", ((Character) value));
        } else if (value instanceof Boolean
                || value instanceof Integer || value instanceof Long
                || value instanceof Byte || value instanceof Short
                || value instanceof Float || value instanceof Double) {
           return value.toString();
        } else if (value instanceof Date) {
            long time = ((Date) value).getTime();
            return String.format("new Date(%s)", time);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            return convertToString(collection);
        } else if (value.getClass().isArray()) {
            List<?> list = Arrays.asList((Object[]) value);
            return convertToString(list);
        } else if (value instanceof Map) {
            return convertToString((Map<?, ?>) value);
        } else {
            return convertToString(value.toString());
        }
    }

    private static String convertToString(Collection<?> objectStream) {
        return String.format("[ %s ]", objectStream.stream()
                .map(JavaScriptVariableConverter::convertToString)
                .collect(Collectors.joining(", ")));
    }

    private static String convertToString(Map<?, ?> map) {
        return String.format("{ %s }", map.entrySet().stream()
                .map(entry -> {
                    String key = convertToString(entry.getKey());
                    String valueString = convertToString(entry.getValue());

                    return String.format("%s : %s", key, valueString);
                })
                .collect(Collectors.joining(", ")));
    }

}
