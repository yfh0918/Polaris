package com.polaris.core.pojo;

import static com.google.common.base.Preconditions.checkNotNull;

public class Server {

	private String ip;
	private Integer port = 80;
	private Integer weight = 1;//default
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		checkNotNull(ip);
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		checkNotNull(port);
		this.port = port;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public Server(String ip) {
		setIp(ip);
	}
	public Server(String ip, Integer port) {
		setIp(ip);
		setPort(port);
	}
	public Server(String ip, Integer port, Integer weight) {
		setIp(ip);
		setPort(port);
		this.weight = weight;
	}
	
	/**
	 * Creates a new {@link Sever} for the given elements.
	 *
	 * @param ip
	 * @param port
	 * @return
	 */
	public static Server of(String ip) {
		return new Server(ip);
	}
	public static Server of(String ip, Integer port) {
		return new Server(ip, port);
	}
	public static Server of(String ip, Integer port, Integer weight) {
		return new Server(ip, port,weight);
	}
	
	@Override
	public String toString() {
		return ip+":"+port;
	}
	
	@Override
	public boolean equals(Object server) {
		if (!(server instanceof Server) || server == null) {
			return false;
		}
		return ip.equals(((Server)server).getIp()) && port.equals(((Server)server).getPort());
	}
}
