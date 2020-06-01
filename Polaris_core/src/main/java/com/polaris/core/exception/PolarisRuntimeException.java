package com.polaris.core.exception;

/**
 * Polaris runtime exception.
 *
 * @author 
 */
public class PolarisRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final String ERROR_MESSAGE_FORMAT = "errCode: %d, errMsg: %s ";

    private int errCode;

    public PolarisRuntimeException(int errCode) {
        super();
        this.errCode = errCode;
    }

    public PolarisRuntimeException(int errCode, String errMsg) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg));
        this.errCode = errCode;
    }

    public PolarisRuntimeException(int errCode, Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
    }

    public PolarisRuntimeException(int errCode, String errMsg, Throwable throwable) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg), throwable);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
