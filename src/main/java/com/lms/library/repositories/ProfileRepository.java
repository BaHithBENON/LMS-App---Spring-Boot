package com.lms.library.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelProfile;

@Repository
public interface ProfileRepository extends JpaRepository<ModelProfile, Long> {
	@Query("SELECT p FROM ModelProfile p WHERE p.user.role = 'USER'")
    List<ModelProfile> findAllByUserRoleUser();
}
