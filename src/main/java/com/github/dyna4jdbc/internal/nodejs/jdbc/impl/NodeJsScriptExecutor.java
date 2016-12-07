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


import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractAutoCloseableJdbcObject;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class NodeJsScriptExecutor extends AbstractAutoCloseableJdbcObject implements OutputCapturingScriptExecutor {

    private static final String GLOBAL_SCOPE_NAME = "GLOBAL";

    private static final String NODE_JS_CONSOLE_LOG_DISPATCH_METHOD = "__dyna4jdbc_console_log";

    private static final String REBIND_CONSOLE_TO_DISPATCH_METHOD_JAVASCRIPT =
            String.format("var console = {}; console.log = %s;", NODE_JS_CONSOLE_LOG_DISPATCH_METHOD);


    private final String conversionCharset;
    private final IOHandlerFactory ioHandlerFactory;

    private volatile boolean isCancelled = false;

    private final Object lockObject = new Object();

    private AtomicReference<PrintWriter> stdOutPrintWriterRef = new AtomicReference<>();
    private AtomicReference<PrintWriter> errorOutPrintWriterRef = new AtomicReference<>();

    private Map<String, Object> executionContext = new HashMap<>();

    private final JavaVoidCallback writeCallback = (receiver, parameters) -> {

        try {

            String string = parameters.getString(0);
            PrintWriter printWriter = stdOutPrintWriterRef.get();

            printWriter.println(string);

            boolean isError = printWriter.checkError();
            if (isError) {
                throw new IOException("Write failed: " + string);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error writing to output", e);
        }
    };

    NodeJsScriptExecutor(NodeJsConnection connection, Configuration configuration) {
        super(connection);

        conversionCharset = configuration.getConversionCharset();
        ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
    }

    @Override
    public final void executeScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        synchronized (lockObject) {

            try (PrintWriter stdOutPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
                 PrintWriter errorOutPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true)) {

                stdOutPrintWriterRef.set(stdOutPrintWriter);
                errorOutPrintWriterRef.set(errorOutPrintWriter);

                String actualScript = String.format("%s%n%n%s", REBIND_CONSOLE_TO_DISPATCH_METHOD_JAVASCRIPT, script);

                executeScript(actualScript, variables);

                if (stdOutputStream instanceof NoOpOutputStream) {
                    final boolean writtenToOutputStream =
                            ((NoOpOutputStream) stdOutputStream).isWriteCalled();

                    if (writtenToOutputStream) {
                        throw JDBCError.USING_STDOUT_FROM_UPDATE.raiseUncheckedException();
                    }
                }

            } finally {

                stdOutPrintWriterRef.set(null);
                errorOutPrintWriterRef.set(null);
            }
        } // end of synchronized (lockObject) block

    }

    private void executeScript(String script, Map<String, Object> variables) {

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("dyna4jdbc_nodejs", ".tmp");
            Files.write(tempFile, script.getBytes(conversionCharset),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            executeScriptFromFile(tempFile, variables);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                try {
                    if (tempFile.toFile().exists()) {
                        Files.delete(tempFile);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void executeScriptFromFile(Path tempFile, Map<String, Object> variables) {
        NodeJS theNodeJs = NodeJS.createNodeJS();

        theNodeJs.getRuntime().registerJavaMethod(writeCallback, NODE_JS_CONSOLE_LOG_DISPATCH_METHOD);

        V8 runtime = theNodeJs.getRuntime();
        final V8Object global = (V8Object) runtime.get(GLOBAL_SCOPE_NAME);

        try {
            putVariables(variables, runtime);


            HashSet<String> beforeExecutionGlobalKeys = new HashSet<>(Arrays.asList(global.getKeys()));

            theNodeJs.exec(tempFile.toFile());

            while (theNodeJs.isRunning()) {
                if (isCancelled) {
                    throw JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST.raiseUncheckedException();
                }

                theNodeJs.handleMessage();
            }

            HashSet<String> afterExecutionGlobalKeys = new HashSet<>(Arrays.asList(global.getKeys()));

            HashSet<String> globalUserKeys = new HashSet<>(afterExecutionGlobalKeys);
            globalUserKeys.removeAll(beforeExecutionGlobalKeys);

            for (String key : globalUserKeys) {

                Object value = V8ObjectUtils.getValue(global, key);

                executionContext.put(key, value);
            }

        } finally {
            if (global != null) {
                global.release();
            }

            theNodeJs.release();
        }
    }

    private void putVariables(Map<String, Object> variables, V8Object object) {

        if (variables != null) {
            executionContext.putAll(variables);
        }


        for (Map.Entry<String, Object> entry : executionContext.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                object.addNull(key);
            } else if (value instanceof String) {
                object.add(key, (String) value);
            } else if (value instanceof Integer) {
                object.add(key, (Integer) value);
            } else if (value instanceof Double) {
                object.add(key, (Double) value);
            } else if (value instanceof Boolean) {
                object.add(key, (Boolean) value);
            } else {
                object.add(key, object.toString());
            }
        }
    }


    @Override
    public void cancel() throws CancelException {
        isCancelled = true;
    }

    String getVersion() {
        return V8.getV8Version();
    }
}
