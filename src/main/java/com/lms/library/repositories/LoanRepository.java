package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelLoan;

@Repository
public interface LoanRepository extends JpaRepository<ModelLoan, Long> {

}
