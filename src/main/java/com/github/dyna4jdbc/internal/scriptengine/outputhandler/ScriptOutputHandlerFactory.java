package com.github.dyna4jdbc.internal.scriptengine.outputhandler;

public interface ScriptOutputHandlerFactory {

	SingleResultSetScriptOutputHandler newSingleResultSetScriptOutputHandler(String script);
	MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(String script);
	UpdateScriptOutputHandler newUpdateScriptOutputHandler(String script);
}