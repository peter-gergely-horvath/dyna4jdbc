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

 
package com.github.dyna4jdbc.internal;

/**
 * <p>
 * Thrown when a user script attempts to write to an
 * {@code OutputStream} is not supposed to write to.</p>
 *
 * <p>
 * This class extends {@code Error} so that a JVM script,
 * that catches {@code Exception}s, will NOT also catch it:
 * a failed write attempt will result in the abortion of
 * the user script and this {@code Error} being propagated
 * back to our code, where it is dealt with appropriately.
 * </p>
 */
public class OutputDisabledError extends Error {

    private static final long serialVersionUID = 1L;

    public OutputDisabledError(String msg) {
        super(msg);
    }

}
