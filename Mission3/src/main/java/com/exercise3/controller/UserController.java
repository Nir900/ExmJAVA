package com.exercise3.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.exercise3.dto.UpdateUserDto;
import com.exercise3.dto.UserDto;
import com.exercise3.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model)
    {
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto dto = userService.findByUsername(auth.getName());
        model.addAttribute("user", dto);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto dto = userService.findByUsername(auth.getName());
        UpdateUserDto user = new UpdateUserDto();
        user.setUsername(dto.getUsername());
        model.addAttribute("updateUserDto", user);
        return "user/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("updateUserDto") UpdateUserDto dto, BindingResult br, Model model)
    {
        if (br.hasErrors())
            return "user/edit-profile";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.updateProfile(auth.getName(), dto);
        return "redirect:/user/profile";
    }

    @GetMapping("/settings")
    public String settings()
    {
        return "user/settings";
    }

    @GetMapping("/activity")
    public String activity()
    {
        return "user/activity";
    }
}
