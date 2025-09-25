package com.jjj.controller;

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jjj.dto.CreateUserDto;
import com.jjj.dto.RoleDto;
import com.jjj.dto.UpdateUserDto;
import com.jjj.dto.UserDto;
import com.jjj.service.RoleService;
import com.jjj.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService)
    {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model)
    {
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("roles", roleService.listRoles());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model)
    {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model)
    {
        model.addAttribute("createUserDto", new CreateUserDto());
        model.addAttribute("allRoles", roleService.listRoles());

        return "admin/create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("createUserDto") CreateUserDto dto, BindingResult br, Model model)
    {
        if (br.hasErrors()) {
            model.addAttribute("allRoles", roleService.listRoles());
            return "admin/create-user";
        }
        try {
            userService.createUser(dto);
            return "redirect:/admin/users";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("allRoles", roleService.listRoles());
            return "admin/create-user";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model)
    {
        UserDto user = userService.findById(id);
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles().stream().map(RoleDto::getName).collect(Collectors.toSet()));
        model.addAttribute("updateUserDto", dto);
        model.addAttribute("userId", id);
        model.addAttribute("allRoles", roleService.listRoles());
        model.addAttribute("uRoles", user.getRoles().stream().map(RoleDto::getName).collect(Collectors.toList()));
        return "admin/edit-user";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable("id") Long id, @ModelAttribute("updateUserDto") UpdateUserDto dto, BindingResult br, Model model)
    {
        if (dto.getPassword() != null && !dto.getPassword().isBlank() && dto.getPassword().length() < 6) {
            br.rejectValue(
                "password",
                "error.password",
                "Password must be at least 6 characters"
            );
        }

        if (br.hasErrors()) {
            model.addAttribute("allRoles", roleService.listRoles());
            model.addAttribute("userId", id);
            model.addAttribute("uRoles", dto.getRoles());
            return "admin/edit-user";
        }
        
        userService.updateUser(id, dto, true);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/roles")
    public String roles(Model model)
    {
        model.addAttribute("roles", roleService.listRoles());
        return "admin/roles";
    }

    @GetMapping("/reports")
    public String reports(Model model)
    {
        model.addAttribute("reportData", "...");
        return "admin/reports";
    }
}