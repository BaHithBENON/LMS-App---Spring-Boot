package com.lms.library.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelLoan;

@Repository
public interface LoanRepository extends JpaRepository<ModelLoan, Long> {
	List<ModelLoan> findByDueDate(Date dueDate);
}
