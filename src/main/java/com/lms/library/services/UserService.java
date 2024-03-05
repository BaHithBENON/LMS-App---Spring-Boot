package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelUser;
import com.lms.library.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
	
   	@PostConstruct
   	public void postConstruct() {
	   	ModelUser user = userRepository.findByUsernameOrEmail("admin", "saazhal@gmail.com");
	   	ModelUser user2 = userRepository.findByUsernameOrEmail("@anuhska", "anushka@gmail.com");
	   	if(user == null) {
	   		System.out.println("Il n'existe pas");
	   		user = new ModelUser();
	   		user.setRole(UserRole.ADMIN);
	   		user.setUsername("admin");
	   		user.setEmail("saazhal@gmail.com");
	   		user.setPassword(passwordEncoder.encode("password"));
	   		
	   		user2 = new ModelUser();
	   		user2.setRole(UserRole.USER);
	   		user2.setUsername("@anushka");
	   		user2.setEmail("anushka@gmail.com");
	   		user2.setPassword(passwordEncoder.encode("password"));
	   		
	   		save(user);
	   		save(user2);
   		} else {
	   		System.out.println("Il existe");
   		}
   	}
   	
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    
    public ModelUser findByEmail(String email) {
        return userRepository.findByUsernameOrEmail(null, email);
    }
    
    public ModelUser findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

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
