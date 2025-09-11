package com.exercise3.mapper;

import org.springframework.stereotype.Component;

import com.exercise3.dto.RoleDto;
import com.exercise3.entity.Role;

@Component
public class RoleMapper {
    public static RoleDto toDto(Role role) 
    {
        if (role == null)
            return null;
        
        return new RoleDto(role.getId(), role.getName(), role.getDescription());
    }
}
