package com.eoboard.security.handler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String MemberId;
    private String message;
}
