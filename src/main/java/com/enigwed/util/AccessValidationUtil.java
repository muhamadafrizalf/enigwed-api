package com.enigwed.util;

import com.enigwed.constant.ERole;
import com.enigwed.constant.SErrorMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.entity.WeddingOrganizer;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
public class AccessValidationUtil {

    public void validateUser(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

    public void validateUserOrAdmin(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }
}
