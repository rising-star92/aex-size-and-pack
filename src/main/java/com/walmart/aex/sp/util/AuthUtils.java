package com.walmart.aex.sp.util;

import com.walmart.aex.security.model.AuditUserInfo;
import com.walmart.aex.security.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@UtilityClass
public class AuthUtils {

    public static String getAuthenticatedUserName() {
        User authenticatedUser = (User) Optional.ofNullable(SecurityContextHolder.getContext()).flatMap(context -> Optional.ofNullable(context.getAuthentication()).map(Authentication::getPrincipal)).orElse(null);
        return Optional.ofNullable(authenticatedUser).flatMap(authUser -> Optional.ofNullable(authUser.getAuditUserInfo()).map(AuditUserInfo::getAuthenticatedUserName)).orElse(null);
    }

}
