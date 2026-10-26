package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.Role;
import com.handloom.marketplace.repository.CategoryRepository;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public HomeController(ProductRepository productRepository, CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("artisans", userRepository.findByRoleAndEnabledTrueOrderByIdAsc(Role.ARTISAN)
                .stream().limit(3).collect(Collectors.toList()));
        return "index";
    }
}
