package com.polaris.container.gateway.dnssec4j;

/**
 * Exception thrown when there's a problem with a DNS signature.
 */
public class DNSSECException extends Exception {

    private static final long serialVersionUID = 6321271843178593498L;
    
    public DNSSECException() {
        super();
    }
    
    public DNSSECException(final String reason) {
        super(reason);
    }
    
    public DNSSECException(final String reason, final Throwable cause) {
        super(reason, cause);
    }
}
