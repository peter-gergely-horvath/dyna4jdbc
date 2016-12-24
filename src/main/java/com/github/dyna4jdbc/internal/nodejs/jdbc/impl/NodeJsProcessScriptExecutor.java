package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutor;


class NodeJsProcessScriptExecutor extends DefaultExternalProcessScriptExecutor {

    private static final OutputStream VARIABLE_SET_OUTPUT_STREAM = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            System.err.write(b);
        }
    };
    // TODO: replace with the following:
    /*new DisallowAllWritesOutputStream(
            "Writing to standard output while a variable is being set is unexpected");*/
    
    
    private String replInitScript;

    
    NodeJsProcessScriptExecutor(Configuration configuration, String replInitScript) {
        super(configuration);
        this.replInitScript = replInitScript;
    }

    @Override
    public void executeScriptUsingStreams(String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream, OutputStream errorOutputStream) throws ScriptExecutionException {
        
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                String stringRepresentation = convertToString(value);

                String statement = String.format("%s = %s;", key, stringRepresentation);

                super.executeScriptUsingStreams(statement, Collections.emptyMap(),
                        VARIABLE_SET_OUTPUT_STREAM, VARIABLE_SET_OUTPUT_STREAM);
            }

        }

        super.executeScriptUsingStreams(script.replace("\n",  " ").replace("\r",  " "),
                variables, stdOutOutputStream, errorOutputStream);
    }

    private String convertToString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof java.lang.String || value instanceof java.lang.Character) {
            return String.format("'%s'", ((String) value).replace("[^\\]'", "\\'"));
        } else if (value instanceof Integer || value instanceof java.lang.Boolean
                || value instanceof java.lang.Byte || value instanceof java.lang.Short
                || value instanceof java.lang.Integer || value instanceof java.lang.Long
                || value instanceof java.lang.Float || value instanceof java.lang.Double) {
           return value.toString();
        } else if (value instanceof Date) {
            long time = ((Date) value).getTime();
            return String.format("new Date(%s)", time);
        } else if (value instanceof Collection) {
            List<String> transformed = ((Collection<?>) value).stream()
                .map(this::convertToString)
                .collect(Collectors.toList());
            return String.format("[ %s ]", String.join(", ", transformed));
        } else if (value.getClass().isArray()) {
            List<String> transformed = Arrays.asList((Object[]) value).stream()
                    .map(this::convertToString)
                    .collect(Collectors.toList());
            return String.format("[ %s ]", String.join(", ", transformed));
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;

            List<String> mapEntriesAsString = map.entrySet().stream()
                .map(entry -> {
                            String key = convertToString(entry.getKey());
                            String valueString = convertToString(entry.getValue());

                            return String.format("%s = %s;", key, valueString);
                })
                .collect(Collectors.toList());
            return String.format("{ %s }", String.join(", ", mapEntriesAsString));
        } else {
            return convertToString(value.toString());
        }
    }

    @Override
    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        throw new ScriptExecutionException(
                "Node.js process exited unexpetedly! Cannot execute script: " + script);

    }


    @Override
    protected Process createProcess(String script, Map<String, Object> variables) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Arrays.asList("node", "-e", replInitScript));

        if (variables != null) {

            Map<String, String> environment = processBuilder.environment();

            variables.entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                Object value = entry.getValue();

                String valueString = String.valueOf(value);

                environment.put(key, valueString);
            });
        }

        return processBuilder.start();
    }

}
