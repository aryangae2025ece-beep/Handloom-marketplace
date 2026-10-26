package com.handloom.marketplace.config;

import com.handloom.marketplace.entity.Role;
import com.handloom.marketplace.repository.CartItemRepository;
import com.handloom.marketplace.repository.UserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/** Puts "currentUser" and "cartCount" in every page so the navbar can show them. */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UserRepository userRepository;
    private final CartItemRepository cartRepository;

    public GlobalModelAdvice(UserRepository userRepository, CartItemRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @ModelAttribute
    public void addGlobals(Model model, Principal principal) {
        if (principal == null) {
            return;
        }
        userRepository.findByEmail(principal.getName()).ifPresent(user -> {
            model.addAttribute("currentUser", user);
            if (user.getRole() == Role.CUSTOMER) {
                Long count = cartRepository.countItems(user.getId());
                model.addAttribute("cartCount", count == null ? 0L : count);
            }
        });
    }
}
