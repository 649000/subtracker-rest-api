package com.subtracker.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FirebaseJwtAuthenticationConverterTest {

    private final FirebaseJwtAuthenticationConverter converter = new FirebaseJwtAuthenticationConverter();

    @Test
    void extractAuthorities_withRolesList_shouldReturnRoleAuthorities() {
        Jwt jwt = new Jwt(
                "token123",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("roles", List.of("admin", "user"))
        );

        Collection<GrantedAuthority> authorities = converter.extractAuthorities(jwt);

        List<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        assertTrue(authorityStrings.contains("ROLE_ADMIN"));
        assertTrue(authorityStrings.contains("ROLE_USER"));
    }

    @Test
    void extractAuthorities_withMixedTypesInRolesList_shouldFilterNonStrings() {
        Jwt jwt = new Jwt(
                "token123",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("roles", Arrays.asList("manager", 123, null))
        );

        Collection<GrantedAuthority> authorities = converter.extractAuthorities(jwt);

        List<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        assertTrue(authorityStrings.contains("ROLE_MANAGER"));
        assertEquals(1, authorityStrings.size());
    }

    @Test
    void extractAuthorities_withNoRolesClaim_shouldReturnEmptyOrScopeAuthorities() {
        Jwt jwt = new Jwt(
                "token123",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user123")
        );

        Collection<GrantedAuthority> authorities = converter.extractAuthorities(jwt);
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void extractAuthorities_withNonListRolesClaim_shouldNotAddRoles() {
        Jwt jwt = new Jwt(
                "token123",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("roles", "not-a-list")
        );

        Collection<GrantedAuthority> authorities = converter.extractAuthorities(jwt);
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void extractAuthorities_withTokenValue_shouldReturnExpectedAuthorities() {
        Jwt jwt = new Jwt(
                "token123",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user123", "roles", List.of("admin"))
        );

        Collection<GrantedAuthority> authorities = converter.extractAuthorities(jwt);
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
