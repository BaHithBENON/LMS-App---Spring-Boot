package com.lms.library.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        // Vérifiez le rôle de l'utilisateur et déterminez l'URL de redirection
        String userRole = authentication.getAuthorities().stream()
        		//.filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("");
        //System.out.println("____" + userRole);

        if ("ADMIN".equals(userRole)) {
            return "/admin/dashboard";
        } else if ("USER".equals(userRole)) {
            return "/guest/dashboard";
        } else {
        	return "/error";
        }
    }
}
