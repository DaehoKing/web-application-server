package model;

/**
 * Created by dapa56 on 2018. 1. 11..
 */
public class RequestLine {
	HttpMethod method;
	String url;
	String httpVersion;

	public RequestLine(String method, String url, String httpVersion) {
		this.method = HttpMethod.valueOf(method.toUpperCase());
		this.url = url;
		this.httpVersion = httpVersion;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getHttpVersion() {
		return httpVersion;
	}
}
