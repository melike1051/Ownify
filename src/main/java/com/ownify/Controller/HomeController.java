package com.ownify.Controller;

import com.ownify.Entity.Product;
import com.ownify.Entity.Category;
import com.ownify.Service.ProductService;
import com.ownify.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product> featuredProducts = productService.getAllProducts();
        
        model.addAttribute("categories", categories);
        model.addAttribute("featuredProducts", featuredProducts);
        return "index";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String q, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        
        List<Product> searchResults = productService.searchProducts(q);
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", q);
        return "search-results";
    }
    
    @GetMapping("/category/{id}")
    public String categoryProducts(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        
        List<Product> products = productService.getProductsByCategoryId(id);
        Category category = categoryService.getCategoryById(id).orElse(null);
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("currentCategory", category);
        model.addAttribute("categories", categories);
        return "category-products";
    }
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        
        model.addAttribute("product", product);
        return "product-detail";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/faqs")
    public String faqs() {
        return "faqs";
    }
    
    @GetMapping("/customer-support")
    public String customerSupport() {
        return "customer-support";
    }
}
