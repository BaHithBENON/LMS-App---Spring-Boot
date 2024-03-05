package com.lms.library.models;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="books")
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
    private String code;
    private int copies;
    
    @OneToMany(mappedBy = "book")
    private List<ModelLoan> loans;

	@OneToMany(mappedBy = "book")
	private List<ModelReservation> reservations;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
    private ModelFile cover;
	
	@Transient
	private String imageBase64;
    
	public ModelBook(Long id, String title, String description, Integer year, String authors, String thumbnail,
			String category, String code, int copies, List<ModelLoan> loans, List<ModelReservation> reservations,
			ModelFile cover) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.code = code;
		this.copies = copies;
		this.loans = loans;
		this.reservations = reservations;
		this.cover = cover;
	}

	public ModelBook(Long id, String title, String description, Integer year, String authors, String thumbnail,
			String category, int copies, List<ModelLoan> loans, List<ModelReservation> reservations, ModelFile cover) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.copies = copies;
		this.loans = loans;
		this.reservations = reservations;
		this.cover = cover;
	}

	public ModelBook(Long id, String title, String description, Integer year, String authors, String thumbnail,
			String category, int copies, List<ModelLoan> loans, List<ModelReservation> reservations) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.copies = copies;
		this.loans = loans;
		this.reservations = reservations;
	}

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

	/**
	 * @return the loans
	 */
	public List<ModelLoan> getLoans() {
		return loans;
	}

	/**
	 * @param loans the loans to set
	 */
	public void setLoans(List<ModelLoan> loans) {
		this.loans = loans;
	}

	/**
	 * @return the reservations
	 */
	public List<ModelReservation> getReservations() {
		return reservations;
	}

	/**
	 * @param reservations the reservations to set
	 */
	public void setReservations(List<ModelReservation> reservations) {
		this.reservations = reservations;
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
	 * @return the imageBase64
	 */
	public String getImageBase64() {
		return imageBase64;
	}

	/**
	 * @param imageBase64 the imageBase64 to set
	 */
	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
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
