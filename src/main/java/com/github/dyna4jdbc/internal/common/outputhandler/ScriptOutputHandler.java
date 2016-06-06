package com.github.dyna4jdbc.internal.common.outputhandler;

import java.io.OutputStream;

public interface ScriptOutputHandler {

    int NO_RESULTS_UPDATE_COUNT = 0;

    OutputStream getOutOutputStream();
    OutputStream getErrorOutputStream();
}
