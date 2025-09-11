package com.exercise3.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exercise3.dto.CreateUserDto;
import com.exercise3.dto.UpdateUserDto;
import com.exercise3.dto.UserDto;
import com.exercise3.entity.Role;
import com.exercise3.entity.User;
import com.exercise3.exception.ConflictException;
import com.exercise3.exception.ResourceNotFoundException;
import com.exercise3.mapper.UserMapper;
import com.exercise3.repository.RoleRepository;
import com.exercise3.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder)
    {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto)
    {
        userRepo.findByUsername(dto.getUsername()).ifPresent(user -> {
            throw new ConflictException("Username already exists: " + dto.getUsername());
        });

        User toSave = UserMapper.fromCreateDto(dto);
        toSave.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Role> resolved = new HashSet<>();
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            Role userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role USER not found"));
            resolved.add(userRole);
        } else {
            resolved = dto.getRoles().stream()
                .map(rn -> roleRepo.findByName(rn)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + rn)))
                .collect(Collectors.toSet());
        }
        toSave.setRoles(resolved);

        User saved = userRepo.save(toSave);
        return UserMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id)
    {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        
        return UserMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers()
    {
        return userRepo.findAll().stream()
            .map(UserMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id)
    {
        if (!userRepo.existsById(id))
            throw new ResourceNotFoundException("User not found: " + id);
        
        userRepo.deleteById(id);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto dto, boolean isAdmin)
    {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            userRepo.findByUsername(dto.getUsername()).ifPresent(existing -> {
                if (!existing.getId().equals(id))
                    throw new ConflictException("Username already exists: " + dto.getUsername());
            });
            user.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        if (dto.getEnabled() != null && isAdmin)
            user.setEnabled(dto.getEnabled());

        if (dto.getRoles() != null && isAdmin) {
            Set<Role> resolved = dto.getRoles().stream()
                .map(rn -> roleRepo.findByName(rn)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + rn)))
                .collect(Collectors.toSet());
            user.setRoles(resolved);
        }

        User saved = userRepo.save(user);
        return UserMapper.toDto(saved);
    }

    @Transactional
    public UserDto updateProfile(String currentUsername, UpdateUserDto dto)
    {
        User user = userRepo.findByUsername(currentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));
        
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            userRepo.findByUsername(dto.getUsername()).ifPresent(existing -> {
                throw new ConflictException("Username already exists: " + dto.getUsername());
            });
            user.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) 
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        User saved = userRepo.save(user);
        return UserMapper.toDto(saved);
    }

    @Transactional
    public UserDto findByUsername(String username)
    {
        User user = userRepo.findByUsernameWithRoles(username)
            .orElseThrow(() -> new ResourceNotFoundException("Username not found: " + username));
        
        return UserMapper.toDto(user);
    }
}
