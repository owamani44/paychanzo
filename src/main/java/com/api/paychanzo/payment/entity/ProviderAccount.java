package com.api.paychanzo.payment.entity;

import com.api.paychanzo.business.entity.Business;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_account")
@Getter
@Setter
public class ProviderAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private PaymentProvider paymentProvider;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "merchant_reference")
    private String merchantReference;

    @Column(name = "encrypted_credentials")
    private String encryptedCredentials;

    @Column(name = "environment")
    @Enumerated(EnumType.STRING)
    private ProviderEnvironment environment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = Instant.now();
    }

}