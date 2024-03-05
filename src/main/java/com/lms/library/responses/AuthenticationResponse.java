package com.lms.library.responses;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse {
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("redirect_url")
	private String redirectUrl;
	
	UserDetails userDetails;
	
	public AuthenticationResponse(String accessToken, String refreshToken, String redirectUrl,
			UserDetails userDetails) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.redirectUrl = redirectUrl;
		this.userDetails = userDetails;
	}

	public AuthenticationResponse(String accessToken, String refreshToken, UserDetails userDetails) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.userDetails = userDetails;
	}

	public AuthenticationResponse(String accessToken, String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public AuthenticationResponse() {
		super();
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return the userDetails
	 */
	public UserDetails getUserDetails() {
		return userDetails;
	}

	/**
	 * @param userDetails the userDetails to set
	 */
	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}

	/**
	 * @return the redirectUrl
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 * @param redirectUrl the redirectUrl to set
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	
}
