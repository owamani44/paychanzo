package com.api.paychanzo.business.config;

import com.api.paychanzo.business.dto.BusinessPrincipal;
import com.api.paychanzo.business.entity.BusinessApiKey;
import com.api.paychanzo.business.service.BusinessApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class BusinessApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final BusinessApiKeyService businessApiKeyService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authorizationHeader.substring(7);

        String[] parts = apiKey.split("\\.", 2);

        if (parts.length != 2 ||
                parts[0].isBlank() ||
                parts[1].isBlank()) {
            response.sendError(401, "Invalid API key");
            return;
        }

        String keyPrefix = parts[0];
        String secret = parts[1];

        try {
            BusinessApiKey apiKeyRecord =
                    businessApiKeyService.validateApiKey(keyPrefix, secret);

            Authentication authentication =
                    createAuthentication(apiKeyRecord);

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(401, "Invalid API key");
        }
    }

    private Authentication createAuthentication(BusinessApiKey apiKeyRecord) {

        BusinessPrincipal principal = new BusinessPrincipal();
        principal.setBusinessId(apiKeyRecord.getBusiness().getId());
        principal.setApiKeyId(apiKeyRecord.getId());
        principal.setBusinessCode(apiKeyRecord.getBusiness().getBusinessCode());

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()
        );
    }
}