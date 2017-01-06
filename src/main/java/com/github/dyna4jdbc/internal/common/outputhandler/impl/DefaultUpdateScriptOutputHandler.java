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

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.util.io.DisallowAllWritesOutputStream;
import com.github.dyna4jdbc.internal.common.util.io.SQLWarningSinkOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;

/**
 * @author Peter G. Horvath
 */
public class DefaultUpdateScriptOutputHandler implements UpdateScriptOutputHandler {

    private final SQLWarningSinkOutputStream stdErr;

    public DefaultUpdateScriptOutputHandler(Configuration configuration, SQLWarningSink warningSink) {

        this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
    }

    //CHECKSTYLE.OFF: DesignForExtension
    @Override
    public OutputStream getOutOutputStream() {
        return new DisallowAllWritesOutputStream("Writing to standard output from an UPDATE call is not allowed");
    }

    @Override
    public OutputStream getErrorOutputStream() {
        return stdErr;
    }
    //CHECKSTYLE.ON: DesignForExtension
}

