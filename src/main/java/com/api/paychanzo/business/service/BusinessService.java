package com.api.paychanzo.business.service;

import com.api.paychanzo.business.dto.BusinessDTO;
import com.api.paychanzo.business.dto.BusinessRequest;
import com.api.paychanzo.business.entity.AccountStatus;
import com.api.paychanzo.business.entity.Business;
import com.api.paychanzo.business.repo.BusinessRepo;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessService {

    private final BusinessRepo businessRepo;

    public BusinessDTO registerBusiness(BusinessRequest request){
        if(businessRepo.existsByEmailIgnoreCase(request.email())){
            throw new DuplicateRequestException("Business with this email already exists");
        }

        if(businessRepo.existsByPhoneNumber(request.phoneNumber())){
            throw new DuplicateRequestException("Business with the same number already exists");
        }
        Business newBusiness = new Business();
        newBusiness.setBusinessName(request.businessName());
        newBusiness.setPhoneNumber(request.phoneNumber());
        newBusiness.setEmail(request.email());
        newBusiness.setBusinessCode(generateBusinessCode(request.businessName()));
        newBusiness.setStatus(AccountStatus.PENDING_REVIEW);

        Business saved = businessRepo.save(newBusiness);
        return BusinessDTO.from(saved);
    }

    public void activateBusinessAccount(UUID id){
        Business b = businessRepo.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Business not found")
        );

        if(b.getStatus()==AccountStatus.ACTIVE){
            throw new IllegalStateException("Business has already been activated");
        };

        b.setStatus(AccountStatus.ACTIVE);
        businessRepo.save(b);
    }

    public String generateBusinessCode(String businessName) {
        StringBuilder code = new StringBuilder();

        for (String word: businessName.trim().split("\\s+")){
            if(!word.isEmpty()){
                code.append(word.charAt(0));
            }
        }
        String baseCode = code.toString().toUpperCase();
        String businessCode = baseCode;

        Integer counter = Integer.valueOf(1);
        while (businessRepo.existsByBusinessCodeIgnoreCase(businessCode)) {
            businessCode = baseCode + String.format("%03d" , counter);
            counter++;
        }
        return businessCode;
    }
}
