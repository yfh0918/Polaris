package com.polaris.extension.cache;

import org.springframework.expression.EvaluationException;

class VariableNotAvailableException extends EvaluationException {  
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;  
  
    public VariableNotAvailableException(String name) {  
        super("Variable '" + name + "' is not available");  
        this.name = name;  
    }  
  
  
    public String getName() {  
        return name;  
    }  
}  