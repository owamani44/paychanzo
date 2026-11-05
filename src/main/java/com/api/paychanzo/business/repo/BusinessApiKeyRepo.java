package com.api.paychanzo.business.repo;

import com.api.paychanzo.business.entity.Business;
import com.api.paychanzo.business.entity.BusinessApiKey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessApiKeyRepo extends JpaRepository<BusinessApiKey, UUID> {

    @Query("SELECT k FROM BusinessApiKey k JOIN FETCH k.business WHERE k.id = :id")
    Optional<BusinessApiKey> findByIdWithBusiness(@Param("id") UUID businessId);

    @Query("""
    SELECT k FROM BusinessApiKey k
    JOIN FETCH k.business
    WHERE k.keyPrefix = :keyPrefix
    """)
    Optional<BusinessApiKey> findByKeyPrefix(@Param("keyPrefix") String keyPrefix);
}
