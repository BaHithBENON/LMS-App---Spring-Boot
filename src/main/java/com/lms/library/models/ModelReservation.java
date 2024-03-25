package com.lms.library.models;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name="reservations")
public class ModelReservation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id")
    //@JsonBackReference
    private ModelBook book;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private ModelUser user;
    
    private Date reservationDate;
    
    private int copies;

	public ModelReservation(Long id, ModelBook book, ModelUser user, Date reservationDate) {
		super();
		this.id = id;
		this.book = book;
		this.user = user;
		this.reservationDate = reservationDate;
	}

	public ModelReservation() {
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
	 * @return the reservationDate
	 */
	public Date getReservationDate() {
		return reservationDate;
	}

	/**
	 * @param reservationDate the reservationDate to set
	 */
	public void setReservationDate(Date reservationDate) {
		this.reservationDate = reservationDate;
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

	@Override
	public int hashCode() {
		return Objects.hash(book, id, reservationDate, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelReservation)) {
			return false;
		}
		ModelReservation other = (ModelReservation) obj;
		return Objects.equals(book, other.book) && Objects.equals(id, other.id)
				&& Objects.equals(reservationDate, other.reservationDate) && Objects.equals(user, other.user);
	}
    
    
}
