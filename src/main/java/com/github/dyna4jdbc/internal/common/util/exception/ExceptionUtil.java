package com.github.dyna4jdbc.internal.common.util.exception;

public class ExceptionUtil {

	public static String getRootCauseMessage(Throwable t) {
		
		Throwable cause = t;
		while(true) {
			
			Throwable parent = cause.getCause();
			if(parent == null) {
				break;
			} 
			
			cause = parent;
		}
		return cause.getMessage();
	}
	
}
