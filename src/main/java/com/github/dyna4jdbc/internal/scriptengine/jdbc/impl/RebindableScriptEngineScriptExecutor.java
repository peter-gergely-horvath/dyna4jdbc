package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;


import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RebindableScriptEngineScriptExecutor implements ScriptEngineScriptExecutor {

    private static final Pattern INTERPRETER_COMMAND_PATTERN =
            Pattern.compile("(.*)(?:\\n|\\r)*(?:\\s*)(?<!jdbc:)dyna4jdbc:(.*)(?:\\n|\\r)?(.*)",
                    Pattern.CASE_INSENSITIVE & Pattern.MULTILINE & Pattern.DOTALL);

    private static final int BEFORE_INTERPRETER_COMMAND_GROUP = 1;
    private static final int INTERPRETER_COMMAND_GROUP = 2;
    private static final int AFTER_INTERPRETER_COMMAND_GROUP = 3;

    private final AtomicReference<ScriptEngineScriptExecutor> delegateRef = new AtomicReference<>();

    private final ConcurrentHashMap<String, ScriptEngineScriptExecutor> scriptExecutorMap = new ConcurrentHashMap<>();

    private final BasicScriptEngineScriptExecutorFactory scriptEngineScriptExecutorFactory;

    RebindableScriptEngineScriptExecutor(ScriptEngineScriptExecutor delegate, Configuration configuration) {

        this.delegateRef.set(delegate);
        this.scriptEngineScriptExecutorFactory = BasicScriptEngineScriptExecutorFactory.getInstance(configuration);
    }

    @Override
    public void cancel() throws CancelException {
        getDelegate().cancel();
    }

    @Override
    public void setVariables(Map<String, Object> variables) {
        getDelegate().setVariables(variables);
    }

    @Override
    public Map<String, Object> getVariables() {
        return getDelegate().getVariables();
    }

    @Override
    public String getSystemName() {
        return getDelegate().getSystemName();
    }

    @Override
    public String getHumanFriendlyName() {
        return getDelegate().getHumanFriendlyName();
    }

    @Override
    public String getVersion() {
        return getDelegate().getVersion();
    }

    @Override
    public void executeScriptUsingStreams(
            String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        if (!INTERPRETER_COMMAND_PATTERN.matcher(script).matches()) {

            executeWithDelegate(script, variables, stdOutOutputStream, errorOutputStream);

        } else {

            Matcher matcher = INTERPRETER_COMMAND_PATTERN.matcher(script);
            while (matcher.find()) {
                String beforeCommand = matcher.group(BEFORE_INTERPRETER_COMMAND_GROUP);
                String interpreterCommandString = matcher.group(INTERPRETER_COMMAND_GROUP);
                String afterCommand = matcher.group(AFTER_INTERPRETER_COMMAND_GROUP);

                InterpreterCommand interpreterCommand = Arrays.stream(InterpreterCommand.values())
                        .filter(it -> it.canHandle(interpreterCommandString))
                        .findFirst()
                        .orElseThrow(() ->
                                new ScriptExecutionException(
                                        "Cannot process as interpreter command: " + interpreterCommandString));


                if (beforeCommand != null && !"".equals(beforeCommand.trim())) {

                    executeWithDelegate(beforeCommand, variables, stdOutOutputStream, errorOutputStream);
                }

                String parameters = interpreterCommandString.trim().substring(interpreterCommand.commandName.length());
                interpreterCommand.parseParametersAndExecute(parameters, this);

                if (afterCommand != null && !"".equals(afterCommand.trim())) {

                    executeWithDelegate(afterCommand, variables, stdOutOutputStream, errorOutputStream);
                }
            }
        }
    }

    private void executeWithDelegate(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        getDelegate().executeScriptUsingStreams(
                script, variables, stdOutOutputStream, errorOutputStream);
    }



    private enum InterpreterCommand {
        SET_SCRIPTENGINE("set-ScriptEngine") {
            @Override
            protected void parseParametersAndExecute(
                    String parameters, RebindableScriptEngineScriptExecutor context)
                    throws ScriptExecutionException {

                String scriptEngineName = parameters.trim();
                if ("".equals(scriptEngineName.trim())) {
                    throw new ScriptExecutionException(
                            this.commandName + ": Missing mandatory parameter: ScriptEngineName");
                }


                context.rebindDelegate(scriptEngineName);
            }
        };

        //CHECKSTYLE.OFF: VisibilityModifier: should be visible to enum fields
        protected final String commandName;
        //CHECKSTYLE.ON: VisibilityModifier

        InterpreterCommand(String commandName) {
            this.commandName = commandName;
        }

        protected boolean canHandle(String commandLine) {
            return commandLine != null && commandLine.trim().startsWith(commandName);
        }

        protected abstract void parseParametersAndExecute(
                String parameters, RebindableScriptEngineScriptExecutor context)
                throws ScriptExecutionException;
    }

    private void rebindDelegate(String newScriptExecutorSystemName) {

        ScriptEngineScriptExecutor previousScriptExecutor = getDelegate();

        String systemName = previousScriptExecutor.getSystemName();

        scriptExecutorMap.put(systemName, previousScriptExecutor);

        ScriptEngineScriptExecutor newScriptExecutor = scriptExecutorMap.computeIfAbsent(newScriptExecutorSystemName,

                scriptEngineName -> {
                    try {
                        return this.scriptEngineScriptExecutorFactory.newScriptEngineScriptExecutor(scriptEngineName);
                    } catch (SQLException | MisconfigurationException ex) {
                        throw JDBCError.LOADING_SCRIPTENGINE_FAILED.raiseUncheckedException(ex, scriptEngineName);
                    }
                });

        newScriptExecutor.setVariables(previousScriptExecutor.getVariables());

        if (!delegateRef.compareAndSet(previousScriptExecutor, newScriptExecutor)) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    String.format("failed to compareAndSet(%s, %s)", previousScriptExecutor, newScriptExecutor));
        }
    }

    private ScriptEngineScriptExecutor getDelegate() {
        ScriptEngineScriptExecutor theDelegate = delegateRef.get();
        if (theDelegate == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE
                    .raiseUncheckedException("theDelegate is null");
        }
        return theDelegate;
    }
}
