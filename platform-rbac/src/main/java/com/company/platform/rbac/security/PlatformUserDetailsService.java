package com.company.platform.rbac.security;

import com.company.platform.common.exception.BusinessException;
import com.company.platform.common.exception.ErrorCode;
import com.company.platform.rbac.entity.UserEntity;
import com.company.platform.rbac.service.UserService;
import java.util.HashSet;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlatformUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public PlatformUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return new PlatformUserDetails(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            new HashSet<>(userService.listPermissionCodes(user.getId())),
            true
        );
    }
}
