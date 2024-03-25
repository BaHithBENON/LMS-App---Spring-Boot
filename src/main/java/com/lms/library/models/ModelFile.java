package com.lms.library.models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="files")
public class ModelFile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    private String filePath;
    

	private String imageBase64;
    
    @Lob
    private byte[] content;
    
    @OneToOne(mappedBy = "cover")
    @JsonBackReference
    private ModelProfile profile;
    
    @OneToOne(mappedBy = "cover")
    @JsonBackReference
    private ModelBook book;

    public ModelFile(String filePath, byte[] content, ModelProfile profile, ModelBook book) {
		super();
		this.filePath = filePath;
		this.content = content;
		this.profile = profile;
		this.book = book;
	}

	public ModelFile(String filePath, byte[] content) {
		super();
		this.filePath = filePath;
		this.content = content;
	}

	public ModelFile(String filePath) {
        this.filePath = filePath;
    }

    public ModelFile() {
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
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * @return the profile
	 */
	public ModelProfile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(ModelProfile profile) {
		this.profile = profile;
	}

	/**
	 * @return the book
	 */
	public ModelBook getBook() {
		return book;
	}

	/**
	 * @param book the book to set
	 */
	public void setBook(ModelBook book) {
		this.book = book;
	}
	
	// Getter et setter pour filePathString
    public Path getFileByPath() {
        return Paths.get(filePath);
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath.toString();
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
		return Objects.hash(book, content, filePath, id, profile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelFile)) {
			return false;
		}
		ModelFile other = (ModelFile) obj;
		return Objects.equals(book, other.book) && Objects.equals(content, other.content)
				&& Objects.equals(filePath, other.filePath) && Objects.equals(id, other.id)
				&& Objects.equals(profile, other.profile);
	}
    

    // Ajoutez d'autres méthodes pour manipuler le fichier selon vos besoins
}