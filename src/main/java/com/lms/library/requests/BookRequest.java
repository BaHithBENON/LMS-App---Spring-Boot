package com.lms.library.requests;

public class BookRequest {
	Long id; 
	String title;
	String description;
	String category;
	Integer year;
	String authors;
	String code;
	Integer copies;
	
	
	public BookRequest(Long id, String title, String description, String category, Integer year, String authors,
			String code, Integer copies) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.year = year;
		this.authors = authors;
		this.code = code;
		this.copies = copies;
	}
	
	public BookRequest(String title, String description, String category, Integer year, String authors,
			String code, Integer copies) {
		super();
		this.title = title;
		this.description = description;
		this.category = category;
		this.year = year;
		this.authors = authors;
		this.code = code;
		this.copies = copies;
	}

	public BookRequest() {
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the authors
	 */
	public String getAuthors() {
		return authors;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(String authors) {
		this.authors = authors;
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
	 * @return the copies
	 */
	public Integer getCopies() {
		return copies;
	}

	/**
	 * @param copies the copies to set
	 */
	public void setCopies(Integer copies) {
		this.copies = copies;
	}
	
	
}
