package com.lms.library.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
	@Column(length = 1024)
	private String description;
    private Integer year;
    private String authors;
    private String thumbnail;
    private String category;
    private String code;
    private int copies;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ModelLoan> loans;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	@JsonBackReference
	private List<ModelReservation> reservations;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id")
	@JsonBackReference
    private ModelFile cover;
	
	@Transient
	private String imageBase64;
	
	public ModelBook(String title, String description, Integer year, String authors, String thumbnail,
			String category, String code, int copies) {
		super();
		this.title = title;
		this.description = description;
		this.year = year;
		this.authors = authors;
		this.thumbnail = thumbnail;
		this.category = category;
		this.code = code;
		this.copies = copies;
	}
    
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
	 * @param loan the loans to add
	 */
	public void addLoan(ModelLoan loan) {
		if(loans == null) {
			loans = new ArrayList<ModelLoan>();
		}
		
		loans.add(loan);
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
	 * @param reservation the reservations to add
	 */
	public void addReservation(ModelReservation reservation) {
		if(reservations == null) {
			reservations = new ArrayList<ModelReservation>();
		}
		
		reservations.add(reservation);
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
	
	@Override
	public String toString() {
		return "ModelBook [id=" + id + ", title=" + title + ", description=" + description + ", year=" + year
				+ ", authors=" + authors + ", thumbnail=" + thumbnail + ", category=" + category + ", code=" + code
				+ ", copies=" + copies + ", loans=" + loans + ", reservations=" + reservations + ", cover=" + cover
				+ ", imageBase64=" + imageBase64 + "]";
	}

	public static List<ModelBook> getStaticBookList() {
        return Arrays.asList(
                new ModelBook("To Kill a Mockingbird", "A powerful exploration of racial injustice and moral growth.", 1960, "Harper Lee", "mockingbird.jpg", "Novel", "MKD1960", 10),
                new ModelBook("1984", "A dystopian novel set in a totalitarian state.", 1949, "George Orwell", "1984.jpg", "Novel", "ORW1949", 15),
                new ModelBook("The Great Gatsby", "A novel about the American Dream and the decadence of the Jazz Age.", 1925, "F. Scott Fitzgerald", "gatsby.jpg", "Novel", "GAT1925", 20),
                new ModelBook("Pride and Prejudice", "A novel that explores the themes of class, marriage, and love.", 1813, "Jane Austen", "pride.jpg", "Novel", "PAJ1813", 12),
                new ModelBook("The Catcher in the Rye", "A novel that explores themes of adolescence, identity, and the loss of innocence.", 1951, "J.D. Salinger", "catcher.jpg", "Novel", "CSL1951", 18),
                new ModelBook("The Hobbit", "A fantasy novel about a hobbit's adventure to reclaim his stolen treasure.", 1937, "J.R.R. Tolkien", "hobbit.jpg", "Fantasy", "HTL1937", 25),
                new ModelBook("The Lord of the Rings", "A fantasy novel about the quest to destroy the One Ring.", 1954, "J.R.R. Tolkien", "lord.jpg", "Fantasy", "LOR1954", 30),
                new ModelBook("The Da Vinci Code", "A thriller novel about a murder mystery and conspiracy theories.", 2003, "Dan Brown", "davinci.jpg", "Thriller", "DVC2003", 22),
                new ModelBook("The Alchemist", "A novel about following one's dreams and finding one's destiny.", 1988, "Paulo Coelho", "alchemist.jpg", "Novel", "ALC1988", 14),
                new ModelBook("The Little Prince", "A philosophical novel about a young prince's journey to Earth.", 1943, "Antoine de Saint-Exupéry", "prince.jpg", "Novel", "LPR1943", 16),
                new ModelBook("The Diary of a Young Girl", "A diary written by Anne Frank during the Nazi occupation of the Netherlands.", 1947, "Anne Frank", "diary.jpg", "Biography", "DYG1947", 11),
                new ModelBook("The Old Man and the Sea", "A novel about an old fisherman's struggle to catch a giant marlin.", 1952, "Ernest Hemingway", "oldman.jpg", "Novel", "OMS1952", 13),
                new ModelBook("The Color Purple", "A novel about the life of a Black woman in the South.", 1982, "Alice Walker", "color.jpg", "Novel", "CPU1982", 17),
                new ModelBook("The Lion, the Witch, and the Wardrobe", "A fantasy novel about a young boy's adventure in a magical world.", 1950, "C.S. Lewis", "wardrobe.jpg", "Fantasy", "LWW1950", 23),
                new ModelBook("The Outsiders", "A novel about two rival gangs in a small town.", 1967, "S.E. Hinton", "outsiders.jpg", "Novel", "OTS1967", 19),
                new ModelBook("The Catcher in the Rye", "A novel that explores themes of adolescence, identity, and the loss of innocence.", 1951, "J.D. Salinger", "catcher.jpg", "Novel", "CSL1951", 21),
                new ModelBook("The Great Gatsby", "A novel about the American Dream and the decadence of the Jazz Age.", 1925, "F. Scott Fitzgerald", "gatsby.jpg", "Novel", "GAT1925", 24),
                new ModelBook("To Kill a Mockingbird", "A powerful exploration of racial injustice and moral growth.", 1960, "Harper Lee", "mockingbird.jpg", "Novel", "MKD1960", 26),
                new ModelBook("1984", "A dystopian novel set in a totalitarian state.", 1949, "George Orwell", "1984.jpg", "Novel", "ORW1949", 27),
                new ModelBook("The Hobbit", "A fantasy novel about a hobbit's adventure to reclaim his stolen treasure.", 1937, "J.R.R. Tolkien", "hobbit.jpg", "Fantasy", "HTL1937", 28)
        );
    }
	
	public String getLimitedDescription() {
		String[] words = this.description.split(" ");
		StringBuilder limitedDescription = new StringBuilder();
        for (int i = 0; i < Math.min(20, words.length); i++) {
            limitedDescription.append(words[i]).append(" ");
        }
        return limitedDescription.toString().trim();
	}
    
}
