package com.ownify.Controller;

import com.ownify.Entity.User;
import com.ownify.Entity.Product;
import com.ownify.Entity.Category;
import com.ownify.Entity.Wishlist;
import com.ownify.Service.UserService;
import com.ownify.Service.ProductService;
import com.ownify.Service.CategoryService;
import com.ownify.Service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession; // ✅ Doğru import - javax yerine jakarta
import java.util.List;

@Controller // ✅ @RestController yerine @Controller
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private WishlistService wishlistService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Product> userProducts = productService.getProductsByUser(user);
        List<Category> categories = categoryService.getAllCategories();
        List<Wishlist> wishlistItems = wishlistService.getUserWishlist(user);
        
        model.addAttribute("user", user);
        model.addAttribute("userProducts", userProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("wishlistItems", wishlistItems);
        
        return "dashboard";
    }
    
    @GetMapping("/dashboard/settings")
    public String dashboardSettings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "dashboard-settings";
    }
    
    @PostMapping("/dashboard/update-profile")
    public String updateProfile(@ModelAttribute User updatedUser, 
                               HttpSession session, 
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Update user fields
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhone(updatedUser.getPhone());
            user.setAddress(updatedUser.getAddress());
            
            User savedUser = userService.updateUser(user);
            session.setAttribute("user", savedUser);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }
        
        return "redirect:/dashboard/settings";
    }
    
    @GetMapping("/dashboard/my-products")
    public String myProducts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Product> userProducts = productService.getProductsByUser(user);
        model.addAttribute("userProducts", userProducts);
        model.addAttribute("user", user);
        
        return "my-products";
    }
    
    @GetMapping("/dashboard/add-product")
    public String addProductPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", new Product());
        
        return "add-product";
    }
    
    @PostMapping("/dashboard/add-product")
    public String addProduct(@ModelAttribute Product product,
                           @RequestParam Long categoryId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            Category category = categoryService.getCategoryById(categoryId).orElse(null);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid category selected");
                return "redirect:/dashboard/add-product";
            }
            
            product.setCategory(category);
            product.setUser(user);
            productService.saveProduct(product);
            
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
            return "redirect:/dashboard/my-products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
            return "redirect:/dashboard/add-product";
        }
    }
}