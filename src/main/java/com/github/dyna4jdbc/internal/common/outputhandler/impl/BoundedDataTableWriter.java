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

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.IOException;

/**
 * @author Peter G. Horvath
 */
public final class BoundedDataTableWriter extends DataTableWriter {

    private final int maxRows;
    private int currentRow = 0;

    BoundedDataTableWriter(Configuration configuration, int maxRows) {
        super(configuration);
        this.maxRows = maxRows;
    }

    protected void nextRow() {
        super.nextRow();

        ++currentRow;
    }

    @Override
    public void write(int b) throws IOException {

        if (currentRow < maxRows) {
            super.write(b);
        }
        /*
        Otherwise, simply swallow the write request: once
        currentRow reaches maxRows, we no longer even convert
        the byte values to strings. */
    }
}
