package com.exercise3.dto;

import java.util.Set;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDto {
    @Size(min = 3, max = 100)
    private String username;

    @Size(min = 6, max = 200)
    private String password;

    private Boolean enabled;

    private Set<String> roles;
}
