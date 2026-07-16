package com.deneme.influencerinsight.rest.requests;

import jakarta.validation.constraints.NotBlank;

/** Hesap silmeyi yanlışlıkla tetiklememek için mevcut parolayı ister. */
public record DeleteAccountRequest(@NotBlank String currentPassword) {
}
