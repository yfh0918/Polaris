package com.polaris.http.dto;

/**
 * @标题: Ftp.java
 * @包名: com.polaris.ftp
 * @描述:
 * @作者: yanghao
 * @时间: Dec 7, 2015 9:57:49 AM
 */
public class Ftp {
	private String ipAddr;// ip地址
	private Integer port;// 端口号
	private String userName;// 用户名
	private String pwd;// 密码
	private String path;// aaa路径
	
	public Ftp(){}
	
	public Ftp(String ipAddr, String userName, String pwd, Integer port){
		this.ipAddr = ipAddr;
		this.userName = userName;
		this.pwd = pwd;
		this.port = port;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Ftp [ipAddr=" + ipAddr + ", port=" + port + ", userName="
				+ userName + ", pwd=" + pwd + ", path=" + path + "]";
	}
}

