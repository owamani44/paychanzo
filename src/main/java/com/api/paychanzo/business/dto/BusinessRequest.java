package com.api.paychanzo.business.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BusinessRequest(@NotBlank String businessName,
                              @Email String email,
                              @NotBlank String phoneNumber) {
}
