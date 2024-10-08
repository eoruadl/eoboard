package com.eoboard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.eoboard.domain.Member;
import com.eoboard.repository.MemberRepository;
import com.eoboard.security.handler.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        String memberId = null;

        try {
            memberId = JWT
            .require(Algorithm.HMAC256("JWT1234!!"))
            .build()
            .verify(jwtToken)
            .getClaim("memberId")
            .asString();

            Member member = memberRepository.findByMemberId(memberId).get();

            CustomUserDetails userDetails = new CustomUserDetails(member);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            ObjectMapper om = new ObjectMapper();
            String errorMessage = "";

            if (e instanceof TokenExpiredException) {
                errorMessage = "토큰이 만료되었습니다.";
            } else if (e instanceof JWTDecodeException) {
                errorMessage = "토큰이 잘못되었습니다.";
            } else if (e instanceof SignatureVerificationException) {
                errorMessage = "토큰이 잘못되었습니다.";
            }

            System.out.println(e.getMessage());

            errorResponse.setMessage(errorMessage);
            errorResponse.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String jsonErrorResponse = om.writeValueAsString(errorResponse);
            response.setCharacterEncoding("utf-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(jsonErrorResponse);

        }
    }
}
