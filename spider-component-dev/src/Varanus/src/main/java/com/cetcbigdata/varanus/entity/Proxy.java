package com.cetcbigdata.varanus.entity;

import java.io.Serializable;
import java.util.Date;

public class Proxy implements Serializable {

	/**
	 * ip地址
	 */
	private String host;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * 代用账号
	 */
	private String username;
	/**
	 * 代理密码
	 */
	private String password;
	/**
	 * 代理类型，HTTP，HTTPS
	 */
	private String type;

	private Date expireTime;

	/**
	 * 是否已使用
	 */
	public static final int USE = 0;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
}
