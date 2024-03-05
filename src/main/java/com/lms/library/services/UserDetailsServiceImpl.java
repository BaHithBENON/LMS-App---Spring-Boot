package com.lms.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelUser;
import com.lms.library.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
    private UserRepository userRepository;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ModelUser user = userRepository.findByUsernameOrEmail(username, username);
        if (user == null) {
        	System.out.println(username + " : OFF");
        	throw new UsernameNotFoundException("User not found");
        }
        //System.out.println(username + " : OK->" + user.getRole());
        /*
        return User.withUsername(user.getUsername())
        		.username(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRole().toString())
				.build();
        */
        return user;
    }
}
