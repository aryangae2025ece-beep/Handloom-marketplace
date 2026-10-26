package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.Product;
import com.handloom.marketplace.entity.Role;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.repository.CategoryRepository;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.repository.UserRepository;
import com.handloom.marketplace.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, ProductRepository productRepository,
                             CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long category,
                       @RequestParam(required = false) String sort,
                       Model model) {
        model.addAttribute("products", productService.search(q, category, sort));
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort == null || sort.isBlank() ? "newest" : sort);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.getActive(id);
        model.addAttribute("product", product);
        model.addAttribute("related", productRepository
                .findTop4ByCategoryIdAndActiveTrueAndIdNot(product.getCategory().getId(), product.getId()));
        return "product-detail";
    }

    /** Public storefront of one artisan: their story and all their products. */
    @GetMapping("/shop/{artisanId}")
    public String artisanShop(@PathVariable Long artisanId, Model model) {
        User artisan = userRepository.findById(artisanId)
                .filter(u -> u.getRole() == Role.ARTISAN && u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artisan not found"));
        model.addAttribute("artisan", artisan);
        model.addAttribute("products",
                productRepository.findByArtisanIdAndActiveTrueOrderByCreatedAtDesc(artisan.getId()));
        return "artisan-shop";
    }
}
