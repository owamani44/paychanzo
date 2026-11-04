package com.api.paychanzo.business.repo;

import com.api.paychanzo.business.entity.BusinessApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BusinessApiKeyRepo extends JpaRepository<BusinessApiKey, UUID> {
}
