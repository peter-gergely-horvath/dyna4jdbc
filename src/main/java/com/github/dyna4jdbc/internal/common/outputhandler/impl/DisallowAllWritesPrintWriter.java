package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import com.github.dyna4jdbc.internal.OutputDisabledError;

public final class DisallowAllWritesPrintWriter extends java.io.PrintWriter {

    private String message;

	protected DisallowAllWritesPrintWriter(String message) {
		super(new DisableOutputWriter(message));
		this.message = message;
    }



    private static final class DisableOutputWriter extends Writer {

        private String message;

    	private DisableOutputWriter(String message) {
        	this.message = message;

        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        	throw new OutputDisabledError(message);

        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }

    }

	protected RuntimeException raiseError() {
		throw new OutputDisabledError(message);
	};


    public void println(double arg) {
        raiseError();
    }



    public void println(char[] arg) {
        raiseError();
    }

    public void println(float arg) {
        raiseError();
    }

    public void println(java.lang.String arg) {
        raiseError();
    }

    public void println(java.lang.Object arg) {
        raiseError();
    }

    public void println(int arg) {
        raiseError();
    }

    public void println(char arg) {
        raiseError();
    }

    public void println(boolean arg) {
        raiseError();
    }

    public void println() {
        raiseError();
    }

    public void println(long arg) {
        raiseError();
    }

    public java.io.PrintWriter append(java.lang.CharSequence arg1, int arg2, int arg3) {
        throw raiseError();
    }

    public java.io.PrintWriter append(java.lang.CharSequence arg) {
    	throw raiseError();
    }

    public java.io.PrintWriter append(char arg) {
    	throw raiseError();
    }


    public java.io.PrintWriter format(Locale l, String format, Object ... args) {
    	throw raiseError();
    }

    public PrintWriter format(String format, Object ... args) {
    	throw raiseError();
    }

    public void write(char[] arg1, int arg2, int arg3) {
        raiseError();
    }

    public void write(java.lang.String arg1, int arg2, int arg3) {
        raiseError();
    }

    public void write(char[] arg) {
        raiseError();
    }

    public void write(int arg) {
        raiseError();
    }

    public void write(java.lang.String arg) {
        raiseError();
    }

    public void print(float arg) {
        raiseError();
    }

    public void print(long arg) {
        raiseError();
    }

    public void print(int arg) {
        raiseError();
    }

    public void print(char arg) {
        raiseError();
    }

    public void print(boolean arg) {
        raiseError();
    }

    public void print(java.lang.Object arg) {
        raiseError();
    }

    public void print(java.lang.String arg) {
        raiseError();
    }

    public void print(char[] arg) {
        raiseError();
    }

    public void print(double arg) {
        raiseError();
    }

    public void close() {

    }

    public void flush() {

    }

    public boolean checkError() {
        return false;
    }
}
