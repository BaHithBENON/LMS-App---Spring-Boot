package com.lms.library.requests;

public class ReservationRequest {
	
	private String username;
	private String code;
	private Long id;	
	private int counter = 0;
	private boolean status = false;
	
	public ReservationRequest(boolean status) {
		super();
		this.status = status;
	}
	
	public ReservationRequest(String username, String code, Long id, int counter) {
		super();
		this.username = username;
		this.code = code;
		this.id = id;
		this.counter = counter;
	}
	
	public ReservationRequest(String username, String code, int counter) {
		super();
		this.username = username;
		this.code = code;
		this.counter = counter;
	}

	public ReservationRequest() {
		super();
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
