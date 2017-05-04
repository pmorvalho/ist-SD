package org.komparator.security;

/** Exception used to signal a problem with security operations. */
public class SecurityException extends Exception {

	private static final long serialVersionUID = 1L;

	public SecurityException() {
	}

	public SecurityException(String message) {
		super(message);
	}

}
