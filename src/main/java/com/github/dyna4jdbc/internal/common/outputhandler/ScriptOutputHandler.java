package com.github.dyna4jdbc.internal.common.outputhandler;

import java.io.OutputStream;

public interface ScriptOutputHandler {

    OutputStream getOutOutputStream();
    OutputStream getErrorOutputStream();
}
