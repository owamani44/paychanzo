package com.api.paychanzo.business.dto;

import com.api.paychanzo.business.entity.AccountStatus;
import com.api.paychanzo.business.entity.Business;

import java.util.UUID;

public record BusinessDTO(UUID id,
                          String businessName,
                          String businessCode, String email,
                          String phoneNumber, AccountStatus status) {
    public static BusinessDTO from(Business business){
        return new BusinessDTO(
          business.getId(),
                business.getBusinessName(),
                business.getBusinessCode(),
                business.getEmail(),
                business.getPhoneNumber(),
                business.getStatus()
        );
    }
}
