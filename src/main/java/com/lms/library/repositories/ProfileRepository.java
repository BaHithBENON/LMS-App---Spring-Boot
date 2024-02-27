package com.lms.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.library.models.ModelProfile;

@Repository
public interface ProfileRepository extends JpaRepository<ModelProfile, Long> {

}
