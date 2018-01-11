package model;

import java.util.Map;

/**
 * Created by dapa56 on 2018. 1. 11..
 */
public class Response {
	byte[] body;
	Map<String, String> headers;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
}
