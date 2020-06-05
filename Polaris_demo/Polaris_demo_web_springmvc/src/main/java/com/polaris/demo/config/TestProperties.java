package com.polaris.demo.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.polaris.core.config.properties.PolarisConfigurationProperties;

@PolarisConfigurationProperties(prefix="test",ext="testP.properties")
@JsonIgnoreProperties(value = { "$$beanFactory"})
public class TestProperties {
	private String address1;
    private String password;
    private int digit;
    private boolean ok;
    private InnerA innerA;
    private List<InnerA> list;
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
	public void setInnerA(InnerA innerA) {
		this.innerA = innerA;
	}
	public InnerA getInnerA() {
		return innerA;
	}
	
	public static class InnerA {
		private String address1;
	    private String password;
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
	}

	public List<InnerA> getList() {
		return list;
	}
	public void setList(List<InnerA> list) {
		this.list = list;
	}

    
}
