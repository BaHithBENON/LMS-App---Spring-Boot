package com.lms.library.models;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * @author Ba'Hith BENON
 *
 */
@Entity
@Table(name="loans")
public class ModelLoan {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "book_id")
    private ModelBook book;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private ModelUser user;
    
    private Date loanDate;
    private Date returnDate;
    private Date dueDate;
    
    private int copies;
    
    @Transient
    private boolean isActive;
    
	public ModelLoan(Long id, ModelBook book, ModelUser user, Date loanDate, Date returnDate, Date dueDate) {
		super();
		this.id = id;
		this.book = book;
		this.user = user;
		this.loanDate = loanDate;
		this.returnDate = returnDate;
		this.dueDate = dueDate;
	}

	public ModelLoan(Long id, ModelBook book, ModelUser user, Date loanDate, Date returnDate) {
		super();
		this.id = id;
		this.book = book;
		this.user = user;
		this.loanDate = loanDate;
		this.returnDate = returnDate;
	}

	public ModelLoan() {
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
	 * @return the loanDate
	 */
	public Date getLoanDate() {
		return loanDate;
	}

	/**
	 * @param loanDate the loanDate to set
	 */
	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	/**
	 * @return the returnDate
	 */
	public Date getReturnDate() {
		return returnDate;
	}

	/**
	 * @param returnDate the returnDate to set
	 */
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	/**
	 * @return the dueDate
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
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
		return Objects.hash(book, id, loanDate, returnDate, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ModelLoan)) {
			return false;
		}
		ModelLoan other = (ModelLoan) obj;
		return Objects.equals(book, other.book) && Objects.equals(id, other.id)
				&& Objects.equals(loanDate, other.loanDate) && Objects.equals(returnDate, other.returnDate)
				&& Objects.equals(user, other.user);
	}
	
	public boolean isActive() {
        Date currentDate = new Date();
        if(dueDate != null)
        	this.isActive = currentDate.before(dueDate);
        else this.isActive = false; 
        
        return this.isActive;
    }
	
}
