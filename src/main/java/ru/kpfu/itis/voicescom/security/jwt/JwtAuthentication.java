package ru.kpfu.itis.voicescom.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;

import java.util.Collection;

public class JwtAuthentication implements Authentication {
    private boolean isAuthenticated = false;
    private final String token;
    private UserDetailsImpl userDetails;

    public JwtAuthentication(String token) {
        this.token = token;
    }

    public JwtAuthentication(String token, UserDetailsImpl userDetails) {
        this.token = token;
        this.userDetails = userDetails;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return token;
    }

    public void setUserDetails(UserDetailsImpl userDetails) {
        this.userDetails = userDetails;
    }
}
