package com.lms.library.requests;

public class ReaderRequest {
	
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private String password;
	private String address;
    private String telephone;
    private String gender;
    
	public ReaderRequest(String firstname, String lastname, String username, String email, String password,
			String address, String telephone, String gender) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.email = email;
		this.password = password;
		this.address = address;
		this.telephone = telephone;
		this.gender = gender;
	}

	public ReaderRequest() {
		super();
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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

	@Override
	public String toString() {
		return "ReaderRequest [firstname=" + firstname + ", lastname=" + lastname + ", username=" + username
				+ ", email=" + email + ", password=" + password + ", address=" + address + ", telephone="
				+ telephone + ", gender=" + gender + "]";
	}
	
	public boolean isEmpty() {
		return (this.getAddress().isBlank() ||
				this.getEmail().isBlank() || 
				this.getFirstname().isBlank() ||
				this.getLastname().isBlank() || 
				this.getGender().isBlank() ||
				this.getPassword().isBlank() || 
				this.getTelephone().isBlank() ||
				this.getUsername().isBlank());
	}
    

}
