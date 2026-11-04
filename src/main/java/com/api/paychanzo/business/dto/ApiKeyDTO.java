package com.api.paychanzo.business.dto;

import com.api.paychanzo.business.entity.ApiKeyStatus;
import com.api.paychanzo.business.entity.BusinessApiKey;

import java.time.Instant;

public record ApiKeyDTO(String keyPrefix, String secretHash,
                        ApiKeyStatus status, Instant expiresAt, Instant createdAt) {

    public static ApiKeyDTO from(BusinessApiKey ak){
        return new ApiKeyDTO(
                ak.getKeyPrefix(),
                ak.getSecretHash(),
                ak.getStatus(),
                ak.getExpiresAt(),
                ak.getCreatedAt()
        );
    }
}
