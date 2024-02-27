package com.lms.library.models;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
public class ModelBook {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String title;
	private String description;
    private Integer year;
    private String authors;
    private String thumbnail;
    private String category;
    private int copies;
    
	public ModelBook(Long id, String title, String description, Integer year, String authors, String thumbnail,
			String category, int copies) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.copies = copies;
	}

	public ModelBook(String title, String description, Integer year, String authors, String thumbnail,
			String category, int copies) {
		super();
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.copies = copies;
	}

	public ModelBook() {
		super();
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
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
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
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
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
	 * @return the copies
	 */
	public int getCopies() {
		return copies;
	}

	/**
	 * @param copies the copies to set
	 */
	public void setCopies(int copies) {
		this.copies = copies;
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

	@Override
	public int hashCode() {
		return Objects.hash(authors, category, copies, description, id, thumbnail, title, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelBook)) {
			return false;
		}
		ModelBook other = (ModelBook) obj;
		return Objects.equals(authors, other.authors) && Objects.equals(category, other.category)
				&& copies == other.copies && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(thumbnail, other.thumbnail)
				&& Objects.equals(title, other.title) && Objects.equals(year, other.year);
	}
	
	
    
}
