package com.polaris.core.pojo;

import static com.google.common.base.Preconditions.checkNotNull;

public class Server {

	private String ip;
	private Integer port = 80;//default
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
	 * @param ipAndPort
	 * @return
	 */
	public static Server of(String ipAndPort) {
		String[] si = ipAndPort.split(":");
		Server server = null;
		if (si.length == 1) {
			server = new Server(si[0], 80, 1);
		} else if (si.length == 2) {
            server = new Server(si[0], Integer.valueOf(si[1]), 1);
        } else if (si.length == 3) {
            server = new Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
        } 
		return server;
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
	public int hashCode() {
		return port * ip.hashCode();
	}
	
	@Override
	public boolean equals(Object server) {
		if (!(server instanceof Server) || server == null) {
			return false;
		}
		return ip.equals(((Server)server).getIp()) && port.equals(((Server)server).getPort());
	}
}
