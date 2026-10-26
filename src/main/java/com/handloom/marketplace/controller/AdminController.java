package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.Category;
import com.handloom.marketplace.entity.OrderStatus;
import com.handloom.marketplace.entity.Role;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.exception.BusinessException;
import com.handloom.marketplace.repository.CategoryRepository;
import com.handloom.marketplace.repository.CustomerOrderRepository;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.repository.UserRepository;
import com.handloom.marketplace.service.OrderService;
import com.handloom.marketplace.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerOrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public AdminController(UserRepository userRepository, ProductRepository productRepository,
                           CategoryRepository categoryRepository, CustomerOrderRepository orderRepository,
                           OrderService orderService, ProductService productService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("customerCount", userRepository.countByRole(Role.CUSTOMER));
        model.addAttribute("artisanCount", userRepository.countByRole(Role.ARTISAN));
        model.addAttribute("productCount", productRepository.countByActiveTrue());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("revenue", orderService.totalRevenue());
        model.addAttribute("recentOrders", orderRepository.findTop5ByOrderByOrderDateDesc());
        return "admin/dashboard";
    }

    // ---------- users ----------

    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAllByOrderByIdAsc());
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            throw new BusinessException("Admin accounts cannot be disabled.");
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        ra.addFlashAttribute("successMsg", user.getName() + (user.isEnabled() ? " was enabled." : " was disabled."));
        return "redirect:/admin/users";
    }

    // ---------- products ----------

    @GetMapping("/admin/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAllByOrderByCreatedAtDesc());
        return "admin/products";
    }

    @PostMapping("/admin/products/{id}/toggle")
    public String toggleProduct(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        productService.setActive(id, active);
        ra.addFlashAttribute("successMsg", active ? "Product is visible again." : "Product hidden from the marketplace.");
        return "redirect:/admin/products";
    }

    // ---------- orders ----------

    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAllByOrderByOrderDateDesc());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrder(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes ra) {
        orderService.updateOrderStatus(id, status);
        ra.addFlashAttribute("successMsg", "Order #" + id + " is now " + status.getLabel() + ".");
        return "redirect:/admin/orders";
    }

    // ---------- categories ----------

    @GetMapping("/admin/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "admin/categories";
    }

    @PostMapping("/admin/categories")
    public String addCategory(@RequestParam String name, RedirectAttributes ra) {
        String clean = name == null ? "" : name.trim();
        if (clean.isEmpty() || clean.length() > 80) {
            throw new BusinessException("Category name must be 1 to 80 characters.");
        }
        if (categoryRepository.existsByNameIgnoreCase(clean)) {
            throw new BusinessException("That category already exists.");
        }
        Category c = new Category();
        c.setName(clean);
        categoryRepository.save(c);
        ra.addFlashAttribute("successMsg", "Category \"" + clean + "\" added.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        if (productRepository.countByCategoryId(id) > 0) {
            throw new BusinessException("This category still has products. Move or remove them first.");
        }
        categoryRepository.deleteById(id);
        ra.addFlashAttribute("successMsg", "Category deleted.");
        return "redirect:/admin/categories";
    }
}
