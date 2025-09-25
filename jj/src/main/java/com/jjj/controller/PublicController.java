package com.jjj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import com.jjj.dto.CreateUserDto;
import com.jjj.dto.UserDto;
import com.jjj.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PublicController {
    private final UserService userService;
    
    @GetMapping("/")
    public String home()
    {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage()
    {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model)
    {
        model.addAttribute("createUserDto", new CreateUserDto());

        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("createUserDto") CreateUserDto dto, BindingResult br, Model model)
    {
        if (br.hasErrors())
            return "register";

        try {
            UserDto created = userService.createUser(dto);
            model.addAttribute("registeredUser", created);
            return "register-success";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/about")
    public String about()
    {
        return "about";
    }

    @GetMapping("/contact")
    public String contact()
    {
        return "contact";
    }

    @GetMapping("/403")
    public String forbidden()
    {
        return "403";
    }
}