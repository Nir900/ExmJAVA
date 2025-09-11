package com.exercise3.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.exercise3.dto.CreateUserDto;
import com.exercise3.dto.RoleDto;
import com.exercise3.dto.UserDto;
import com.exercise3.entity.User;

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
