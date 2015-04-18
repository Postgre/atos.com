package com.atos.ws.production.postscanactivities;

public class PODCustomException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;
	 
    public PODCustomException() {
        super();
    }
 
    public PODCustomException(String message) {
        super(message);
        this.message = message;
    }
 
    public PODCustomException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }

}
