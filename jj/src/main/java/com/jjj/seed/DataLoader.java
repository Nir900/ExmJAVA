package com.jjj.seed;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jjj.entity.Role;
import com.jjj.entity.User;
import com.jjj.repository.RoleRepository;
import com.jjj.repository.UserRepository;

@Configuration
public class DataLoader {
    @Bean
    public ApplicationRunner initializer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder)
    {
        return args -> { 
            Role adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new Role("ADMIN", "Administrator")));
            Role userRole = roleRepo.findByName("USER").orElseGet(() -> roleRepo.save(new Role("USER", "Default user")));

            if (userRepo.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("adminpass"));
                admin.setEnabled(true);
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                userRepo.save(admin);

                User regular = new User();
                regular.setUsername("alice");
                regular.setPassword(encoder.encode("alicepass"));
                regular.setEnabled(true);
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(userRole);
                regular.setRoles(userRoles);
                userRepo.save(regular);
            }
        };    
    }
}