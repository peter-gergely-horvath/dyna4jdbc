package com.github.dyna4jdbc.internal;

public class UnexpectedIllegalStateReachedException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	
	public UnexpectedIllegalStateReachedException(String message) {
		super(message);
	}
	
	public static UnexpectedIllegalStateReachedException forMessage(String format, Object... args) {
		return new UnexpectedIllegalStateReachedException(String.format(format, args));
	}
}
