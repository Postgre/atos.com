package com.atos.hook;

public class VinGenerationCustomException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;
	 
    public VinGenerationCustomException() {
        super();
    }
 
    public VinGenerationCustomException(String message) {
        super(message);
        this.message = message;
    }
 
    public VinGenerationCustomException(Throwable cause) {
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
