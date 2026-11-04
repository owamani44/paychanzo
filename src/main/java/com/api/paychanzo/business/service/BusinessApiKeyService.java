package com.api.paychanzo.business.service;

import com.api.paychanzo.business.dto.ApiKeyDTO;
import com.api.paychanzo.business.entity.AccountStatus;
import com.api.paychanzo.business.entity.ApiKeyStatus;
import com.api.paychanzo.business.entity.Business;
import com.api.paychanzo.business.entity.BusinessApiKey;
import com.api.paychanzo.business.repo.BusinessApiKeyRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BusinessApiKeyService {


    private final BusinessApiKeyRepo apiKeyRepo;
    private final EntityManager entityManager;

    String ApiKey;

    private String generateSecret() {

        SecureRandom secureRandom = new SecureRandom();

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashSecret(String secret) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    secret.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public String createApiKey(UUID businessId){

        BusinessApiKey ak = apiKeyRepo.findByIdWithBusiness(businessId).orElseThrow(
                ()->new EntityNotFoundException("Business not found")
        );

        Business business = ak.getBusiness();

        if (business.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Action denied: Business account is " + business.getStatus());
        }

        Instant oneYearFromNow = ZonedDateTime.now(ZoneOffset.UTC)
                .plusYears(1)
                .toInstant();

        String keyPrefix = "PCZ0";
        String secret = generateSecret();
        ApiKey= keyPrefix+secret;
        String hashedSecret = hashSecret(secret);

        ak.setBusiness(entityManager.getReference(Business.class,  businessId ));
        ak.setKeyPrefix(keyPrefix);
        ak.setSecretHash(hashedSecret);
        ak.setStatus(ApiKeyStatus.ACTIVE);
        ak.setExpiresAt(oneYearFromNow);
        apiKeyRepo.save(ak);
        return ApiKey;
    }
}
