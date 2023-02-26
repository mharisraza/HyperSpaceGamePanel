package com.hyperspacegamepanel.controllers.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.hyperspacegamepanel.entities.Order;

@Controller
@RequestMapping("/order")
public class OrderController {

    @GetMapping("")
    public String home(Model m) {
        m.addAttribute("title", "Order Server | HyperSpaceGamePanel");
        m.addAttribute("order", new Order());
        return "user/order.html";
    }

    @PostMapping("/process-order")
    public String processOrder(@ModelAttribute Order order, Model m) {
             System.out.println(order.toString());
        return "redirect:/home";
    }
    
}
