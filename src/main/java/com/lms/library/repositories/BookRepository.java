package com.lms.library.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelBook;

@Repository
public interface BookRepository extends JpaRepository<ModelBook, Long> {

	Optional<ModelBook> findByCode(String code);
    
}
