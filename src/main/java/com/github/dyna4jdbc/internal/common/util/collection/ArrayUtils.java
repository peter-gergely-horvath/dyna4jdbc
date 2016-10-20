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

 
package com.github.dyna4jdbc.internal.common.util.collection;

/**
 * @author Peter G. Horvath
 */
public final class ArrayUtils {

    private ArrayUtils() {
        throw new AssertionError("static utility class");
    }

    public static <T> T tryGetByIndex(T[] array, int index) {
        if (array == null) {
            throw new NullPointerException("argument array cannot be null");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        T result;
        if (array.length > index) {
            result = array[index];
        } else {
            result = null;
        }
        return result;
    }


}
