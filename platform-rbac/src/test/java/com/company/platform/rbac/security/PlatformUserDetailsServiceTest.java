package com.company.platform.rbac.security;

import com.company.platform.rbac.entity.UserEntity;
import com.company.platform.rbac.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PlatformUserDetailsService userDetailsService;

    @Test
    void shouldReflectPermissionChangesWhenReloadingUser() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setUsername("admin");
        user.setPassword("encoded");
        user.setStatus(1);

        when(userService.getByUsername("admin")).thenReturn(user);
        when(userService.listPermissionCodes(10L)).thenReturn(List.of("user:read"), List.of("user:read", "user:write"));

        UserDetails first = userDetailsService.loadUserByUsername("admin");
        UserDetails second = userDetailsService.loadUserByUsername("admin");

        assertEquals(1, first.getAuthorities().size());
        assertEquals(2, second.getAuthorities().size());
    }
}
