package com.handloom.marketplace.controller;

import com.handloom.marketplace.dto.ProductForm;
import com.handloom.marketplace.entity.OrderItem;
import com.handloom.marketplace.entity.OrderStatus;
import com.handloom.marketplace.entity.Product;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.repository.CategoryRepository;
import com.handloom.marketplace.repository.ProductRepository;
import com.handloom.marketplace.service.OrderService;
import com.handloom.marketplace.service.ProductService;
import com.handloom.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ArtisanController {

    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderService orderService;

    public ArtisanController(UserService userService, ProductService productService,
                             ProductRepository productRepository, CategoryRepository categoryRepository,
                             OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderService = orderService;
    }

    @GetMapping("/artisan/dashboard")
    public String dashboard(Principal principal, Model model) {
        User artisan = userService.getByEmail(principal.getName());
        List<Product> products = productRepository.findByArtisanIdAndActiveTrueOrderByCreatedAtDesc(artisan.getId());
        List<OrderItem> items = orderService.itemsForArtisan(artisan);
        long pending = items.stream().filter(i -> i.getStatus() == OrderStatus.PLACED).count();
        model.addAttribute("products", products);
        model.addAttribute("orderCount", items.size());
        model.addAttribute("pendingCount", pending);
        model.addAttribute("revenue", orderService.artisanRevenue(items));
        return "artisan/dashboard";
    }

    // ---------- product CRUD ----------

    @GetMapping("/artisan/products/new")
    public String newProduct(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "artisan/product-form";
    }

    @PostMapping("/artisan/products")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm form, BindingResult result,
                                Principal principal, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
            return "artisan/product-form";
        }
        User artisan = userService.getByEmail(principal.getName());
        Product saved = productService.create(artisan, form);
        ra.addFlashAttribute("successMsg", "\"" + saved.getName() + "\" is now live in the marketplace.");
        return "redirect:/artisan/dashboard";
    }

    @GetMapping("/artisan/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Principal principal, Model model) {
        User artisan = userService.getByEmail(principal.getName());
        Product product = productService.getOwned(id, artisan);
        model.addAttribute("productForm", ProductForm.from(product));
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "artisan/product-form";
    }

    @PostMapping("/artisan/products/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("productForm") ProductForm form,
                                BindingResult result, Principal principal, Model model, RedirectAttributes ra) {
        User artisan = userService.getByEmail(principal.getName());
        if (result.hasErrors()) {
            form.setId(id);
            form.setCurrentImageUrl(productService.getOwned(id, artisan).getImageUrl());
            model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
            return "artisan/product-form";
        }
        Product saved = productService.update(id, artisan, form);
        ra.addFlashAttribute("successMsg", "\"" + saved.getName() + "\" was updated.");
        return "redirect:/artisan/dashboard";
    }

    @PostMapping("/artisan/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        productService.deactivate(id, userService.getByEmail(principal.getName()));
        ra.addFlashAttribute("successMsg", "Product removed from the marketplace.");
        return "redirect:/artisan/dashboard";
    }

    // ---------- orders for my products ----------

    @GetMapping("/artisan/orders")
    public String orders(Principal principal, Model model) {
        User artisan = userService.getByEmail(principal.getName());
        model.addAttribute("items", orderService.itemsForArtisan(artisan));
        return "artisan/orders";
    }

    @PostMapping("/artisan/orders/items/{itemId}/status")
    public String updateStatus(@PathVariable Long itemId, @RequestParam OrderStatus status,
                               Principal principal, RedirectAttributes ra) {
        orderService.updateItemStatus(userService.getByEmail(principal.getName()), itemId, status);
        ra.addFlashAttribute("successMsg", "Order item marked as " + status.getLabel() + ".");
        return "redirect:/artisan/orders";
    }
}
