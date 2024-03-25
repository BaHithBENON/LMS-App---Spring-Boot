package com.lms.library.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="profiles")
public class ModelProfile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    //@JsonBackReference
    private ModelUser user;
    
    private String address;
    private String phoneNumber;
    private String gender;
    private String firstname;
    private String lastname;
    
    @OneToOne
    @JoinColumn(name = "file_id")
    @JsonBackReference
    private ModelFile cover;
    
	public ModelProfile(Long id, ModelUser user, String address, String phoneNumber, String gender, String firstname,
			String lastname, ModelFile cover) {
		super();
		this.id = id;
		this.user = user;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.firstname = firstname;
		this.lastname = lastname;
		this.cover = cover;
	}

	public ModelProfile(Long id, ModelUser user, String address, String phoneNumber, String gender, ModelFile cover) {
		super();
		this.id = id;
		this.user = user;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.cover = cover;
	}

	public ModelProfile(Long id, ModelUser user, String address, String phoneNumber, String gender) {
		super();
		this.id = id;
		this.user = user;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
	}
	
	public ModelProfile(Long id, ModelUser user, String address, String phoneNumber) {
		super();
		this.id = id;
		this.user = user;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	public ModelProfile() {
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the cover
	 */
	public ModelFile getCover() {
		return cover;
	}

	/**
	 * @param cover the cover to set
	 */
	public void setCover(ModelFile cover) {
		this.cover = cover;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, id, phoneNumber, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelProfile)) {
			return false;
		}
		ModelProfile other = (ModelProfile) obj;
		return Objects.equals(address, other.address) && Objects.equals(id, other.id)
				&& Objects.equals(phoneNumber, other.phoneNumber) && Objects.equals(user, other.user);
	}
    
    
}
