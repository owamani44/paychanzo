package com.api.paychanzo.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_provider")
@Getter
@Setter
public class PaymentProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "provider_code")
    private String providerCode;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    private String providerType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProviderStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = Instant.now();
    }
}
