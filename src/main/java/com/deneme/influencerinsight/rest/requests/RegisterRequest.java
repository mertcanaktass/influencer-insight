package com.deneme.influencerinsight.rest.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 72, message = "Şifre en az 8, en fazla 72 karakter olmalı.")
    private String password;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
}
