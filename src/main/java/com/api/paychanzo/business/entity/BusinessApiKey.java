package com.api.paychanzo.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "business_api_key")
@Getter
@Setter
public class BusinessApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "key_prefix")
    private String keyPrefix;

    @Column(name = "secret_hash")
    private String secretHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApiKeyStatus status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}
