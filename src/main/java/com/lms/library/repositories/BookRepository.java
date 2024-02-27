package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelBook;

@Repository
public interface BookRepository extends JpaRepository<ModelBook, Long> {
    
}
