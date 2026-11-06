package com.api.paychanzo.business.service;


import com.api.paychanzo.business.entity.AccountStatus;
import com.api.paychanzo.business.entity.ApiKeyStatus;
import com.api.paychanzo.business.entity.Business;
import com.api.paychanzo.business.entity.BusinessApiKey;
import com.api.paychanzo.business.repo.BusinessApiKeyRepo;
import com.api.paychanzo.business.repo.BusinessRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BusinessApiKeyService {


    private final BusinessApiKeyRepo apiKeyRepo;
    private final BusinessRepo businessRepo;
    private final EntityManager entityManager;


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

    @Transactional
    public String createApiKey(UUID businessId){

        //find business
        Business business = businessRepo.findById(businessId).orElseThrow(
                ()->new EntityNotFoundException("Business not found")
        );


        //validate business status
        if (business.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Action denied: Business account is " + business.getStatus());
        }

        //set expiry time
        Instant expiresAt = Instant.now().plus(365, ChronoUnit.DAYS);

        //generate keyPrefix
        String keyPrefix = "pczo_live_"+ UUID.randomUUID();

        //generate secret
        String secret = generateSecret();

        //create the api key
        String RawApiKey= keyPrefix+"."+secret;

        //hashing the secret
        String hashedSecret = hashSecret(secret);

        BusinessApiKey apiKey = new BusinessApiKey();

        apiKey.setBusiness(business);
        apiKey.setKeyPrefix(keyPrefix);
        apiKey.setSecretHash(hashedSecret);
        apiKey.setStatus(ApiKeyStatus.ACTIVE);
        apiKey.setLastUsedAt(Instant.now());
        apiKey.setExpiresAt(expiresAt);

        //save the api key entity
        apiKeyRepo.save(apiKey);

        return RawApiKey;
    }

    @Transactional
    public BusinessApiKey validateApiKey(
            String keyPrefix,
            String secret
    ) {

        // 1. Find API key
        BusinessApiKey apiKey = apiKeyRepo.findByKeyPrefix(keyPrefix)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid API key")
                );

        // 2. Check status
        if (apiKey.getStatus() != ApiKeyStatus.ACTIVE) {
            throw new BadCredentialsException("Invalid API key");
        }

        // 3. Check expiration
        if (apiKey.getExpiresAt() == null ||
                !apiKey.getExpiresAt().isAfter(Instant.now())) {

            throw new BadCredentialsException("Invalid API key");
        }

        // 4. Hash supplied secret
        String suppliedHash = hashSecret(secret);

        // 5. Compare hashes
        boolean matches = MessageDigest.isEqual(
                HexFormat.of().parseHex(suppliedHash),
                HexFormat.of().parseHex(apiKey.getSecretHash())
        );

        if (!matches) {
            throw new BadCredentialsException("Invalid API key");
        }

        // 6. Update last used
        apiKey.setLastUsedAt(Instant.now());

        return apiKey;
    }


    public void revokeApiKey(UUID id){

    }
}
