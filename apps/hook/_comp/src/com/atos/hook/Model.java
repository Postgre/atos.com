package com.atos.hook;

public enum Model {
	 K(100);
	 
	private int modelNo;
 
	private Model(int modelNo) {
		this.modelNo = modelNo;
	}
	public static String getModelNo(int modelNo) {
		 switch(modelNo) {
		    case 100:
		        return Model.K.name();
		    
		    }
		 return "";
	}
}
