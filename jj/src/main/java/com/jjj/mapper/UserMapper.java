package com.jjj.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.jjj.dto.CreateUserDto;
import com.jjj.dto.RoleDto;
import com.jjj.dto.UserDto;
import com.jjj.entity.User;

public class UserMapper {
    public static UserDto toDto(User user)
    {
        if (user == null)
            return null;
        
        Set<RoleDto> roleDtos = user.getRoles().stream()
            .map(RoleMapper::toDto)
            .collect(Collectors.toSet());
        
        return new UserDto(
            user.getId(),
            user.getUsername(), 
            user.isEnabled(),
            roleDtos
        );
    }

    public static User fromCreateDto(CreateUserDto dto)
    {
        if (dto == null)
            return null;
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEnabled(true);

        return user;
    }
}