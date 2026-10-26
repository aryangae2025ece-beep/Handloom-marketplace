package com.handloom.marketplace.controller;

import com.handloom.marketplace.entity.CartItem;
import com.handloom.marketplace.entity.CustomerOrder;
import com.handloom.marketplace.entity.User;
import com.handloom.marketplace.service.CartService;
import com.handloom.marketplace.service.OrderService;
import com.handloom.marketplace.service.UserService;
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
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model, RedirectAttributes ra) {
        User user = userService.getByEmail(principal.getName());
        List<CartItem> items = cartService.getItems(user);
        if (items.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Your cart is empty. Add something first.");
            return "redirect:/cart";
        }
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        model.addAttribute("user", user);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String address,
                             @RequestParam(required = false) String phone,
                             @RequestParam(required = false) String paymentMethod,
                             Principal principal) {
        User user = userService.getByEmail(principal.getName());
        CustomerOrder order = orderService.placeOrder(user, address, phone, paymentMethod);
        return "redirect:/orders/success/" + order.getId();
    }

    @GetMapping("/orders/success/{id}")
    public String success(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("order", orderService.getForCustomer(id, user));
        return "order-success";
    }

    @GetMapping("/orders")
    public String myOrders(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("orders", orderService.ordersOf(user));
        return "my-orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        User user = userService.getByEmail(principal.getName());
        orderService.cancelByCustomer(id, user);
        ra.addFlashAttribute("successMsg", "Order #" + id + " was cancelled and the items were returned to stock.");
        return "redirect:/orders";
    }
}
