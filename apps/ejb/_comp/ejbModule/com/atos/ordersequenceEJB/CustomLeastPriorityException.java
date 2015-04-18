package com.atos.ordersequenceEJB;

public class CustomLeastPriorityException extends Exception

{
		private static final long serialVersionUID = 1L;
		private String errorMessage;
		

		public String getErrorMessage() 
		{
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) 
		{
			this.errorMessage = errorMessage;
		}
		public CustomLeastPriorityException(String errorMessage)
		{
		super(errorMessage);
		this.errorMessage = errorMessage;
		}
		

}
