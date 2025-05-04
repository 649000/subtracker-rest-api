package com.subtracker.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FirebaseJwtAuthenticationConverter extends JwtAuthenticationConverter {
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

        // Extract custom claims
        Object rolesClaim = jwt.getClaim("roles"); // e.g., ["admin", "user"]
        if (rolesClaim instanceof List<?> rolesList) {
            List<GrantedAuthority> roleAuthorities = rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                    .collect(Collectors.toList());
            authorities.addAll(roleAuthorities);
        }

        // Eg: @PreAuthorize("hasAuthority('ROLE_MANAGER')") in Controller
        return authorities;
    }

}