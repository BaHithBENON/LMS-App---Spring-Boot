package com.lms.library.models;

import com.lms.library.enums.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="tokens")
public class ModelToken {
	
	@Id
	@GeneratedValue
	public Long id;

  	@Column(unique = true)
  	public String token;

  	@Enumerated(EnumType.STRING)
  	public TokenType tokenType = TokenType.BEARER;

  	public boolean revoked;

  	public boolean expired;

  	@ManyToOne(fetch = FetchType.LAZY)
  	@JoinColumn(name = "user_id")
  	public ModelUser user;


	public ModelToken(Long id, String token, TokenType tokenType, boolean revoked, boolean expired, ModelUser user) {
		super();
		this.id = id;
		this.token = token;
		this.tokenType = tokenType;
		this.revoked = revoked;
		this.expired = expired;
		this.user = user;
	}

	public ModelToken() {
		super();
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
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the tokenType
	 */
	public TokenType getTokenType() {
		return tokenType;
	}

	/**
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * @return the revoked
	 */
	public boolean isRevoked() {
		return revoked;
	}

	/**
	 * @param revoked the revoked to set
	 */
	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	/**
	 * @return the expired
	 */
	public boolean isExpired() {
		return expired;
	}

	/**
	 * @param expired the expired to set
	 */
	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	/**
	 * @return the user
	 */
	public ModelUser getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(ModelUser user) {
		this.user = user;
	}
  	
  	
}
