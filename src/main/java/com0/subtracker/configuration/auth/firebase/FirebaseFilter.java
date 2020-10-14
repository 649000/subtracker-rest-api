package com0.subtracker.configuration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class FirebaseFilter extends OncePerRequestFilter {

    private static String HEADER_NAME = "X-Authorization-Firebase";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String xAuth = httpServletRequest.getHeader(HEADER_NAME);
        if (StringUtils.isBlank(xAuth)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        } else {
//            Task<FirebaseToken> authTask = FirebaseAuth.getInstance().verifyIdToken(xAuth);
//            try {
////                FirebaseTokenHolder holder = firebaseService.parseToken(xAuth);
////
////                String userName = holder.getUid();
////
////                Authentication auth = new FirebaseAuthenticationToken(userName, holder);
////                SecurityContextHolder.getContext().setAuthentication(auth);
//
                filterChain.doFilter(httpServletRequest, httpServletResponse);
//            } catch (FirebaseTokenInvalidException e) {
//                throw new SecurityException(e);
//            }
        }
    }
}
