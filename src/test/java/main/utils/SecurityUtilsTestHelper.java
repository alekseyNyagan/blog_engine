package main.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class SecurityUtilsTestHelper {

    public static void setAuthenticatedUser(String email, List<String> roles) {
        var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        var user = new User(email, "password", authorities);
        var auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}

