package com.github.dyna4jdbc.internal.common.outputhandler;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Peter Horvath
 */
public interface IOHandlerFactory {

    PrintWriter newPrintWriter(OutputStream out, boolean autoFlush);

    OutputStreamWriter newOutputStreamWriter(OutputStream stdOutputStream);

    PrintStream newPrintStream(OutputStream outputStream);
}
