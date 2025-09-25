package com.jjj.mapper;

import org.springframework.stereotype.Component;

import com.jjj.dto.RoleDto;
import com.jjj.entity.Role;

@Component
public class RoleMapper {
    public static RoleDto toDto(Role role) 
    {
        if (role == null)
            return null;
        
        return new RoleDto(role.getId(), role.getName(), role.getDescription());
    }
}