package com.eoboard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String SECRET_KEY = "JWT1234!!";

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);

        setFilterProcessesUrl("/api/v1/auth/login");

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        ObjectMapper om = new ObjectMapper();

        try {
            LoginRequest login = om.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken
                    .unauthenticated(login.getMemberId(), login.getPassword());
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();

        String jwt = JWT.create()
                .withSubject("JWT token")
                .withExpiresAt(new Date(System.currentTimeMillis() + 6000 * 10)) //10분
                .withClaim("memberId", principal.getUsername())
                .sign(Algorithm.HMAC256(SECRET_KEY));

        response.addHeader("Authorization", "Bearer " + jwt);

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String memberId;
        private String password;

        @Override
        public String toString() {
            return "LoginRequest{" +
                    "memberId='" + memberId + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

}
