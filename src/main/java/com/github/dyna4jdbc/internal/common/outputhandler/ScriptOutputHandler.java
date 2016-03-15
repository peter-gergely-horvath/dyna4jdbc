package com.github.dyna4jdbc.internal.common.outputhandler;

import java.io.PrintWriter;

public interface ScriptOutputHandler {
    PrintWriter getOutPrintWriter();
    PrintWriter getErrorPrintWriter();
}
