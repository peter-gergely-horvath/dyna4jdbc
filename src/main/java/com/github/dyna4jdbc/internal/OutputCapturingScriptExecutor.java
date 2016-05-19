package com.github.dyna4jdbc.internal;

import java.io.Writer;

public interface OutputCapturingScriptExecutor {

    void executeScriptUsingCustomWriters(
            String script, Writer outWriter, Writer errorWriter) throws ScriptExecutionException;
}
