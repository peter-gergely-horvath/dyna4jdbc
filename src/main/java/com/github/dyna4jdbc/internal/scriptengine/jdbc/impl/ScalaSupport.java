package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;

import javax.script.ScriptEngine;
import java.lang.reflect.Method;

/**
 * @author Peter Horvath
 */
class ScalaSupport {

    private static final String SCALA_SCRIPT_ENGINE_CLASS_NAME = "scala.tools.nsc.interpreter.IMain";

    private static final String SETTINGS_METHOD_NAME = "settings";
    private static final String PROCESS_ARGUMENT_METHOD_NAME = "processArgumentString";

    public static final String USEJAVACP_ARGUMENT = "-usejavacp";

    static boolean isScalaScriptEngine(ScriptEngine engine) {
        return engine.getClass().getName().equals(SCALA_SCRIPT_ENGINE_CLASS_NAME);
    }

    static void configureScaleScriptEngine(ScriptEngine engine) {

        try {
            Method settingsRetrievalMethod = engine.getClass().getMethod(SETTINGS_METHOD_NAME);

            Object settingsObject = settingsRetrievalMethod.invoke(engine);

            Method processArgumentStringMethod = settingsObject.getClass().getMethod(
                    PROCESS_ARGUMENT_METHOD_NAME, String.class);

            processArgumentStringMethod.invoke(settingsObject, USEJAVACP_ARGUMENT);

        } catch (Exception e) {
            JDBCError.UNEXPECTED_THROWABLE.raiseUncheckedException(e,
                    "Failed to configure Scala ScriptEngine: " + e.getMessage());
        }
    }
}
