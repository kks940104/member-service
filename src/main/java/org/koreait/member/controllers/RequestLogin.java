package org.koreait.member.controllers;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RequestLogin {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
