package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;
import java.io.Writer;

public class DisallowAllWritesPrintWriter extends java.io.PrintWriter {

    private final String message;

    public static DisallowAllWritesPrintWriter forMessage(String message) {
        return new DisallowAllWritesPrintWriter(message);
    }


    private DisallowAllWritesPrintWriter(String message) {
        super(NoOpWriter.INSTANCE);
        this.message = message;
    }

    private static class NoOpWriter extends Writer {

        private static final NoOpWriter INSTANCE = new NoOpWriter();

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {

        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }

    }



    public void println(double arg) {
        throw new IllegalStateException(message);
    }

    public void println(char[] arg) {
        throw new IllegalStateException(message);
    }

    public void println(float arg) {
        throw new IllegalStateException(message);
    }

    public void println(java.lang.String arg) {
        throw new IllegalStateException(message);
    }

    public void println(java.lang.Object arg) {
        throw new IllegalStateException(message);
    }

    public void println(int arg) {
        throw new IllegalStateException(message);
    }

    public void println(char arg) {
        throw new IllegalStateException(message);
    }

    public void println(boolean arg) {
        throw new IllegalStateException(message);
    }

    public void println() {
        throw new IllegalStateException(message);
    }

    public void println(long arg) {
        throw new IllegalStateException(message);
    }

    public java.io.PrintWriter append(java.lang.CharSequence arg1, int arg2, int arg3) {
        throw new IllegalStateException(message);
    }

    public java.io.PrintWriter append(java.lang.CharSequence arg) {
        throw new IllegalStateException(message);
    }

    public java.io.PrintWriter append(char arg) {
        throw new IllegalStateException(message);
    }

    public java.io.PrintWriter format(java.util.Locale arg1, java.lang.String arg2, java.lang.Object[] arg3) {
        throw new IllegalStateException(message);
    }

    public java.io.PrintWriter format(java.lang.String arg1, java.lang.Object[] arg2) {
        throw new IllegalStateException(message);
    }

    public void write(char[] arg1, int arg2, int arg3) {
        throw new IllegalStateException(message);
    }

    public void write(java.lang.String arg1, int arg2, int arg3) {
        throw new IllegalStateException(message);
    }

    public void write(char[] arg) {
        throw new IllegalStateException(message);
    }

    public void write(int arg) {
        throw new IllegalStateException(message);
    }

    public void write(java.lang.String arg) {
        throw new IllegalStateException(message);
    }

    public void print(float arg) {
        throw new IllegalStateException(message);
    }

    public void print(long arg) {
        throw new IllegalStateException(message);
    }

    public void print(int arg) {
        throw new IllegalStateException(message);
    }

    public void print(char arg) {
        throw new IllegalStateException(message);
    }

    public void print(boolean arg) {
        throw new IllegalStateException(message);
    }

    public void print(java.lang.Object arg) {
        throw new IllegalStateException(message);
    }

    public void print(java.lang.String arg) {
        throw new IllegalStateException(message);
    }

    public void print(char[] arg) {
        throw new IllegalStateException(message);
    }

    public void print(double arg) {
        throw new IllegalStateException(message);
    }

    public void close() {

    }

    public void flush() {

    }

    public boolean checkError() {
        return false;
    }
}
