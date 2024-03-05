package com.lms.library.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelToken;

@Repository
public interface TokenRepository  extends JpaRepository<ModelToken, Long> {

	  @Query(value = """
	      select t from ModelToken t inner join t.user u\s
	      where u.id = :id and (t.expired = false or t.revoked = false)\s
	      """)
	  List<ModelToken> findAllValidTokenByUser(Long id);
	
	  Optional<ModelToken> findByToken(String token);
}