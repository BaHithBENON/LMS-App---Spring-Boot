package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelUser;
import com.lms.library.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<ModelUser> findAll() {
        return userRepository.findAll();
    }

    public ModelUser save(ModelUser user) {
        return userRepository.save(user);
    }

    public ModelUser findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
