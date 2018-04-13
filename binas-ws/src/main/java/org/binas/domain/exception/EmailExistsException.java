package org.binas.domain.exception;

public class EmailExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmailExistsException() {
	}

	public EmailExistsException(String arg0) {
		super(arg0);
	}
}
