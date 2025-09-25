package com.jjj.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 200)
    private String password;

    private Set<String> roles;
    
}