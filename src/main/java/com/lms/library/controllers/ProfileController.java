package com.lms.library.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelProfile;
import com.lms.library.services.ProfileService;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public List<ModelProfile> getAllProfiles() {
        return profileService.findAll();
    }

    @PostMapping
    public ModelProfile createProfile(@RequestBody ModelProfile profile) {
        return profileService.save(profile);
    }

    @GetMapping("/{id}")
    public ModelProfile getProfileById(@PathVariable Long id) {
        return profileService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelProfile updateProfile(@PathVariable Long id, @RequestBody ModelProfile profileDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id) {
        profileService.deleteById(id);
    }
}
