package com.eoboard.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class JwtAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        ErrorResponse errorResponse = new ErrorResponse();
        ObjectMapper om = new ObjectMapper();
        String errorMessage = "";


        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디와 비밀번호를 확인해주세요.";
            errorResponse.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인할 수 없습니다. 관리자에게 문의하세요.";
            errorResponse.setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "아이디를 찾을 수 없습니다.";
            errorResponse.setErrorCode(HttpServletResponse.SC_NOT_FOUND);
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            errorMessage = "알 수없는 오류입니다.";
            errorResponse.setErrorCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        errorResponse.setMessage(errorMessage);
        String jsonErrorResponse = om.writeValueAsString(errorResponse);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonErrorResponse);
    }
}
