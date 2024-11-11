package com.enigwed.util;

import com.enigwed.constant.EReceiver;
import com.enigwed.constant.ERole;
import com.enigwed.constant.SErrorMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.entity.Notification;
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

    public void validateUser(JwtClaim userInfo, Notification notification) throws AccessDeniedException {
        String userCredentialId = notification.getReceiverId() != null ? notification.getReceiverId() : "";
        if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name()) && notification.getReceiver().equals(EReceiver.ADMIN)) {
            return;
        } else if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

}
