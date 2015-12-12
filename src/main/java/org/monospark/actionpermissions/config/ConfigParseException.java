package org.monospark.actionpermissions.config;

public class ConfigParseException extends Exception {

	private static final long serialVersionUID = 7303115744828046555L;

	public ConfigParseException(String msg) {
		super(msg);
	}
	
	public ConfigParseException(Exception e) {
		super(e);
	}
}
