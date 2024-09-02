package com.eoboard.security.handler;

import lombok.*;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private int errorCode;
    private String message;
}
