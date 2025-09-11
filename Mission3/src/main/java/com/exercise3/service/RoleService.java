package com.exercise3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exercise3.dto.RoleDto;
import com.exercise3.entity.Role;
import com.exercise3.exception.ConflictException;
import com.exercise3.mapper.RoleMapper;
import com.exercise3.repository.RoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepo;

    @Transactional(readOnly = true)
    public List<RoleDto> listRoles()
    {
        return roleRepo.findAll().stream()
            .map(RoleMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public RoleDto createRole(String name, String description)
    {
        roleRepo.findByName(name).ifPresent(r -> {
            throw new ConflictException("Role exists: " + name);
        });
        Role role = roleRepo.save(new Role(name, description));

        return RoleMapper.toDto(role);
    }
}
