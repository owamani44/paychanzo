package com.api.paychanzo.business.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BusinessPrincipal {
    private UUID businessId;
    private UUID apiKeyId;
    private String businessCode;
}
