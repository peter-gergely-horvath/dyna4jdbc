package com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ObjectCapturingPrintWriter extends java.io.PrintWriter {


	public ObjectCapturingPrintWriter() {
		super(DisallowAllWritesWriter.INSTANCE);
	}

	static final Object NEW_LINE_PLACEHOLDER = new Object();
	
	
	private LinkedList<Object> capturedObjects = new LinkedList<>();
    
	private Formatter formatter;

	private void printLineInternal(Object x) {
		synchronized (lock) {
			capturedObjects.addLast(x);
			capturedObjects.addLast(NEW_LINE_PLACEHOLDER);
		}
	}
	
	private void printInternal(Object x) {
		synchronized (lock) {
			capturedObjects.addLast(x);
		}
	}
	
	@Override
    public void println(double x) {
    	printLineInternal(x);
    }

    public void println(char[] x) {
    	printLineInternal(x);
    }

    public void println(float x) {
    	printLineInternal(x);
    }

    public void println(java.lang.String x) {
    	printLineInternal(x);
    }

    public void println(java.lang.Object x) {
    	printLineInternal(x);
    }

    public void println(int x) {
    	printLineInternal(x);
    }

    public void println(char x) {
    	printLineInternal(x);
    }

    public void println(boolean x) {
    	printLineInternal(x);
    }
    
    public void println(long x) {
    	printLineInternal(x);
    }

    public void println() {
		synchronized (lock) {
			capturedObjects.addLast(NEW_LINE_PLACEHOLDER);
		}
    }

    public java.io.PrintWriter append(java.lang.CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    public java.io.PrintWriter append(java.lang.CharSequence csq) {
        if (csq == null)
            write("null");
        else
            write(csq.toString());
        return this;
    }

    public java.io.PrintWriter append(char c) {
        write(c);
        return this;
    }

    public PrintWriter format(Locale l, String format, Object ... args) {
            synchronized (lock) {
                if ((formatter == null) || (formatter.locale() != l))
                    formatter = new Formatter(this, l);
                formatter.format(l, format, args);
            }
        return this;
    }

    public java.io.PrintWriter format(String format, Object... args) {
		    synchronized (lock) {
		        if ((formatter == null)
		            || (formatter.locale() != Locale.getDefault()))
		            formatter = new Formatter(this);
		        formatter.format(Locale.getDefault(), format, args);
		    }
		return this;
    }

    public void write(char buf[], int off, int len) {
    	char[] arrayToWrite = new char[len];
    	
    	System.arraycopy(buf, off, arrayToWrite, 0, len);
    	
    	printInternal(arrayToWrite);
    }

    public void write(String str, int off, int len) {
    	
    	if(str.length() == len) {
    		write(str);
    		
    	} else {
    		char arrayToWrite[] = new char[len];
            str.getChars(off, (off + len), arrayToWrite, 0);
            
            printInternal(arrayToWrite);
    	}
    }

    public void write(char[] x) {
    	printInternal(x);
    }

    public void write(int x) {
    	printInternal(x);
    }

    public void write(java.lang.String x) {
    	printInternal(x);
    }

    public void print(float x) {
    	printInternal(x);
    }

    public void print(long x) {
    	printInternal(x);
    }

    public void print(int x) {
    	printInternal(x);
    }

    public void print(char x) {
    	printInternal(x);
    }

    public void print(boolean x) {
    	printInternal(x);
    }

    public void print(Object x) {
    	printInternal(x);
    }

    public void print(String x) {
    	printInternal(x);
    }

    public void print(char[] x) {
    	printInternal(x);
    }

    public void print(double x) {
    	printInternal(x);
    }

    public void close() {
        if(formatter != null) {
        	formatter.close();
        	formatter = null;
        }
    }

    public void flush() {
        // no-op
    }

    public boolean checkError() {
        if(formatter == null) {
        	return false;
        }
        
        return formatter.ioException() != null;
    }
    
    public List<Object> getUnmodifyAbleCapturedObjectList() {
    	return Collections.unmodifiableList(capturedObjects);
    }
}
