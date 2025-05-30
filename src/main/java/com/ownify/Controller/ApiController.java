package com.ownify.Controller;

import com.ownify.Entity.Category;
import com.ownify.Entity.Product;
import com.ownify.Service.CategoryService;
import com.ownify.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/categories/{id}/products")
    public ResponseEntity<List<Product>> getCategoryProducts(@PathVariable Long id) {
        List<Product> products = productService.getProductsByCategoryId(id);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String q) {
        Map<String, Object> response = new HashMap<>();
        
        if (q == null || q.trim().isEmpty()) {
            response.put("products", List.of());
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
        
        List<Product> products = productService.searchProducts(q.trim());
        response.put("products", products);
        response.put("count", products.size());
        response.put("query", q);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/products/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts(@RequestParam(defaultValue = "8") int limit) {
        List<Product> allProducts = productService.getAllProducts();
        
        // Get latest products as featured (you can modify this logic)
        List<Product> featuredProducts = allProducts.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(limit)
                .toList();
        
        return ResponseEntity.ok(featuredProducts);
    }
    
    @GetMapping("/products/random")
    public ResponseEntity<List<Product>> getRandomProducts(@RequestParam(defaultValue = "6") int limit) {
        List<Product> allProducts = productService.getAllProducts();
        
        // Simple random selection (you might want to implement better randomization)
        java.util.Collections.shuffle(allProducts);
        List<Product> randomProducts = allProducts.stream()
                .limit(limit)
                .toList();
        
        return ResponseEntity.ok(randomProducts);
    }
}