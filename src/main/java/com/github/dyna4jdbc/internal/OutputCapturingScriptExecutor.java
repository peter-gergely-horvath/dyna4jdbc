package com.github.dyna4jdbc.internal;

import java.io.OutputStream;

public interface OutputCapturingScriptExecutor {

    void executeScriptUsingStreams(
            String script,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException;

    void cancel() throws CancelException;
}
