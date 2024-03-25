package com.lms.library.api;

public class ApiResponse {
	private int responseCode;
	private Object body;
	private String message;
	
	public ApiResponse(int responseCode, Object body, String message) {
		super();
		this.responseCode = responseCode;
		this.body = body;
		this.message = message;
	}

	public ApiResponse() {
		super();
	}

	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(Object body) {
		this.body = body;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
