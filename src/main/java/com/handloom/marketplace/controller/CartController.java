package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.CartItem;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.service.CartService;
import com.handloom.marketplace.service.UserService;
import com.handloom.marketplace.util.Redirects;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String view(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        List<CartItem> items = cartService.getItems(user);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String add(@PathVariable Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      Principal principal, HttpServletRequest request, RedirectAttributes ra) {
        User user = userService.getByEmail(principal.getName());
        String name = cartService.add(user, productId, quantity);
        ra.addFlashAttribute("successMsg", name + " was added to your cart.");
        return "redirect:" + Redirects.back(request, "/cart");
    }

    @PostMapping("/cart/update/{itemId}")
    public String update(@PathVariable Long itemId, @RequestParam int quantity, Principal principal) {
        cartService.updateQuantity(userService.getByEmail(principal.getName()), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{itemId}")
    public String remove(@PathVariable Long itemId, Principal principal, RedirectAttributes ra) {
        cartService.remove(userService.getByEmail(principal.getName()), itemId);
        ra.addFlashAttribute("successMsg", "Item removed from your cart.");
        return "redirect:/cart";
    }
}
