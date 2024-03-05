package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelProfile;
import com.lms.library.repositories.ProfileRepository;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public List<ModelProfile> findAll() {
        return profileRepository.findAll();
    }
    
    public List<ModelProfile> findAllByUserRoleUser() {
        return profileRepository.findAllByUserRoleUser();
    }

    public ModelProfile save(ModelProfile profile) {
        return profileRepository.save(profile);
    }

    public ModelProfile findById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        profileRepository.deleteById(id);
    }
}
