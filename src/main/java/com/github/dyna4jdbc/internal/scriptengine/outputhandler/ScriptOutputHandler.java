package com.github.dyna4jdbc.internal.scriptengine.outputhandler;

import java.io.PrintWriter;

public interface ScriptOutputHandler {
    PrintWriter getOutPrintWriter();
    PrintWriter getErrorPrintWriter();
}
