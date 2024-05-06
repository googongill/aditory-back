package com.googongill.aditory.service;

import com.googongill.aditory.security.jwt.user.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return Optional.of(principalDetails.getNickname());
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }
}
