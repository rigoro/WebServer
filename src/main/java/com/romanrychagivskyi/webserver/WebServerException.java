package com.romanrychagivskyi.webserver;

/**
 * Custom runtime exception
 */
public class WebServerException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public WebServerException() {
		super();
	}

	public WebServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebServerException(String message) {
		super(message);
	}

}
