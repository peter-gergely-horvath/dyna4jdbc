package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;

public class DisallowAllWritesWriter extends java.io.Writer {

	public static final DisallowAllWritesWriter INSTANCE = new DisallowAllWritesWriter();
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new AssertionError(DisallowAllWritesWriter.class + 
				" does not allow write method to be called.");
	}

	@Override
	public void flush() throws IOException {
		// no-op
	}

	@Override
	public void close() throws IOException {
		// no-op
	}

}
