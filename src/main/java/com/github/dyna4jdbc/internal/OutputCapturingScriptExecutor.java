package com.github.dyna4jdbc.internal;

import java.io.OutputStream;
import java.util.Map;

public interface OutputCapturingScriptExecutor {

    void executeScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException;

    void cancel() throws CancelException;
}
