package com.lms.library.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.library.enums.TokenType;
import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelToken;
import com.lms.library.models.ModelUser;
import com.lms.library.repositories.TokenRepository;
import com.lms.library.repositories.UserRepository;
import com.lms.library.requests.LoginRequest;
import com.lms.library.requests.RegisterRequest;
import com.lms.library.responses.AuthenticationResponse;
import com.lms.library.security.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {
  private UserRepository repository;
  
  @Autowired
  private TokenRepository tokenRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private JwtProvider jwtService;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  public AuthenticationResponse register(RegisterRequest request) {
    ModelUser user = new ModelUser();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(UserRole.USER);
    user.setUsername(request.getUsername());
    
    Authentication auth = new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
    );

    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(auth);
    var refreshToken = jwtService.generateRefreshToken(auth);
    
    saveUserToken(savedUser, jwtToken);
    
    return new AuthenticationResponse(jwtToken, refreshToken);
  }

  public AuthenticationResponse authenticate(LoginRequest request) {
	Authentication auth = new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
    );
	//System.out.println("username : 1");
	//SecurityContextHolder.getContext().setAuthentication(auth);
	System.out.println("username : 2");
    authenticationManager.authenticate(auth);
    System.out.println("username : 3");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("username : 4");
    var user = userService.findByEmail(request.getEmail());
    System.out.println("username : 5");
    var jwtToken = jwtService.generateToken(authentication);
    System.out.println("username : 6");
    var refreshToken = jwtService.generateRefreshToken(authentication);
    System.out.println("username : 7");
    
    revokeAllUserTokens(user);
    System.out.println("username : 8");
    saveUserToken(user, jwtToken);
    System.out.println("username : 9");
    
    return new AuthenticationResponse(jwtToken, refreshToken);
  }

  private void saveUserToken(ModelUser user, String jwtToken) {
    ModelToken token = new ModelToken();
    
    token.setUser(user);
    token.setTokenType(TokenType.BEARER);
    token.setToken(jwtToken);
    token.setExpired(false);
    token.setRevoked(false);
 
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(ModelUser user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    
    if (userEmail != null) {
      var user = this.userService.findByEmail(userEmail);
      
      Authentication auth = new UsernamePasswordAuthenticationToken(
	        user.getEmail(),
	        user.getPassword()
	  );
      
      if (jwtService.isTokenValid(refreshToken, user)) {
    	  
        var accessToken = jwtService.generateToken(auth);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        
        var authResponse = new AuthenticationResponse(accessToken, refreshToken);
        
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

}