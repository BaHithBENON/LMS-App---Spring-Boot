package com.lms.library.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelBook;

@Repository
public interface BookRepository extends JpaRepository<ModelBook, Long> {

	Optional<List<ModelBook>> findByCode(String code);
	
	@Query("SELECT b FROM ModelBook b ORDER BY b.title ASC")
	List<ModelBook> findTopNBooks(@Param("n") int n);
	
	default List<ModelBook> findTopNBooksWithLimit(int n) {
        return findTopNBooks(n).stream().limit(n).collect(Collectors.toList());
    }
    
}
