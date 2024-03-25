package com.lms.library.models;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="notifications")
public class ModelNotification {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private ModelUser user;
    
    private String message;
    private Date sentDate;
    
	public ModelNotification(Long id, ModelUser user, String message, Date sentDate) {
		super();
		this.id = id;
		this.user = user;
		this.message = message;
		this.sentDate = sentDate;
	}

	public ModelNotification() {
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

	/**
	 * @return the sentDate
	 */
	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, message, sentDate, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelNotification)) {
			return false;
		}
		ModelNotification other = (ModelNotification) obj;
		return Objects.equals(id, other.id) && Objects.equals(message, other.message)
				&& Objects.equals(sentDate, other.sentDate) && Objects.equals(user, other.user);
	}
    
    
}
