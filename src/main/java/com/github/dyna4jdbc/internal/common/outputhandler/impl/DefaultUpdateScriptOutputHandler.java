package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.util.io.DisallowAllWritesOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;

/**
 * @author Peter Horvath
 */
final class DefaultUpdateScriptOutputHandler implements UpdateScriptOutputHandler {

    private final SQLWarningSinkOutputStream stdErr;

    DefaultUpdateScriptOutputHandler(Configuration configuration, SQLWarningSink warningSink) {

        this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
    }

    @Override
    public OutputStream getOutOutputStream() {
        return new DisallowAllWritesOutputStream("Writing to to stdout from update is not allowed");
    }

    @Override
    public OutputStream getErrorOutputStream() {
        return stdErr;
    }
}
