package com.ownify.Controller;

import com.ownify.Entity.User;
import com.ownify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession; // ✅ Doğru import
import java.util.Optional;

@Controller // ✅ View döndüren controller için
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password,
                       HttpSession session, 
                       RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.loginUser(email, password);
        if (user.isPresent()) {
            session.setAttribute("user", user.get());
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user, 
                          RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, 
                               RedirectAttributes redirectAttributes) {
        // Password reset logic here
        redirectAttributes.addFlashAttribute("success", "Password reset link sent to your email");
        return "redirect:/login";
    }
}