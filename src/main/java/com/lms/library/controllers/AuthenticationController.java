package com.lms.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelUser;
import com.lms.library.requests.LoginRequest;
import com.lms.library.responses.AuthenticationResponse;
import com.lms.library.services.AuthenticationService;
import com.lms.library.services.UserService;

@Controller
public class AuthenticationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private AuthenticationService authenticationService;

	
	@GetMapping("/login")
    public String loginPage() {
        return "/login/login_2"; // Chemin vers votre fichier login.html dans /resources/templates/auth
    }
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestParam("email") String email,
	        @RequestParam("password") String password
	) {
		return ResponseEntity.ok(authenticationService.authenticate(new LoginRequest(email, password)));
	}
	
	@PostMapping("/tologin")
    public ResponseEntity<String> handleLogin(
		@RequestParam("email") String email,
        @RequestParam("password") String password
    ) {
		System.out.println("username : " + email);
		try {
			// Cherchez l'utilisateur dans la base de donnée avant de vouloir l'authentifier
			ModelUser user = userService.findByEmail(email);
			
			if(user != null) {
				
				Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(user.getEmail(), password)
				);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				Authentication auth_ = SecurityContextHolder.getContext().getAuthentication();
				if(auth_ != null) {
					for (GrantedAuthority authority : auth_.getAuthorities()) {
			            // Ici, vous pouvez vérifier si l'autorité correspond au rôle que vous recherchez
			            // Par exemple, si vous utilisez des rôles sous forme de chaînes de caractères
			            String role = authority.getAuthority();
			            System.out.println("Role: " + role);
			            // Vous pouvez retourner le rôle ou le traiter comme nécessaire
			        }
				}
				
				// Récupérer l'utilisateur authentifié
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				System.out.println(userDetails.getUsername() + " : " + userDetails.getAuthorities().size());
				
				// Utiliser userService pour récupérer l'utilisateur par son email
				
				// Vérifier le rôle de l'utilisateur et rediriger en conséquence
				if (user.getRole() == UserRole.ADMIN) {
					// return new ModelAndView("redirect:/admin/dashboard");
					return ResponseEntity.ok().build();
				} else {
					// return new ModelAndView("redirect:/guest/books");
					return ResponseEntity.ok().build();
				}
			} else {
				return ResponseEntity.notFound().build();
			}
			
	    } catch (BadCredentialsException e) {
	        // Gérer l'erreur de mauvais identifiants
	        //return new ModelAndView("redirect:/login?error=true");
	    	return ResponseEntity.notFound().build();
	    }
    }
}
