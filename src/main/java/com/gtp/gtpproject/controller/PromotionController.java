package com.booknest.booknest.controller;

import com.booknest.booknest.model.Promotion;
import com.booknest.booknest.service.PromotionService;
import com.booknest.booknest.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/promotion")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String listPromotions(Model model) {
        model.addAttribute("promotionList", promotionService.getAllPromotions());
        return "promotion/list";
    }

    @GetMapping("/new")
    public String createPromotionForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("staffList", staffService.getAllStaff());
        return "promotion/form";
    }

    @PostMapping
    public String savePromotion(@Valid @ModelAttribute("promotion") Promotion promotion,
                                BindingResult result,
                                @RequestParam(name = "staffId", required = false) Long staffId,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("staffList", staffService.getAllStaff());
            return "promotion/form";
        }

        if (staffId != null) {
            promotion.setStaff(staffService.getStaffById(staffId).orElseThrow());
        }

        promotionService.savePromotion(promotion);
        return "redirect:/promotion";
    }

    @GetMapping("/edit/{id}")
    public String editPromotionForm(@PathVariable Long id, Model model) {
        model.addAttribute("promotion", promotionService.getPromotionById(id).orElseThrow());
        model.addAttribute("staffList", staffService.getAllStaff());
        return "promotion/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return "redirect:/promotion";
    }
}
