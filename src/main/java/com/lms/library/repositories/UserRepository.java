package com.lms.library.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelUser;

@Repository
public interface UserRepository extends JpaRepository<ModelUser, Long> {
	ModelUser findByUsername(String username);
	ModelUser findByUsernameOrEmail(String username, String email);
	ModelUser findByEmail(String email);
	
	@Query("SELECT u FROM ModelUser u WHERE u.role = 'USER'")
	List<ModelUser> findByRole(UserRole role);
}
