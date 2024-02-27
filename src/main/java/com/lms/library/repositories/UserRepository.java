package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelUser;

@Repository
public interface UserRepository extends JpaRepository<ModelUser, Long> {
	ModelUser findByUsername(String username);
	ModelUser findByUsernameOrEmail(String username, String email);
}
