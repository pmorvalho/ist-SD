package org.komparator.security;

/** Exception used to signal a problem with security operations. */
public class KomparatorSecurityException extends Exception {

	private static final long serialVersionUID = 1L;

	public KomparatorSecurityException() {
	}

	public KomparatorSecurityException(String message) {
		super(message);
	}

}
