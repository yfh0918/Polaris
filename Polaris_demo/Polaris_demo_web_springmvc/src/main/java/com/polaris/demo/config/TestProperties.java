package com.polaris.demo.config;

import com.polaris.core.config.properties.PolarisConfigurationProperties;

@PolarisConfigurationProperties(value="test.test1",file="testP.properties",ignoreInvalidFields=true)
public class TestProperties {
	private String address1;
    private String password;
    private int digit;
    private boolean ok;
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}

    
}
