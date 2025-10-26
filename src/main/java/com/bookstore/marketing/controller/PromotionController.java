package com.bookstore.marketing.controller;

import com.bookstore.marketing.entity.Promotion; // CORRECTED: Use 'model' package
import com.bookstore.marketing.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/promotions")
public class PromotionController {

    // CORRECTED: Constructor Injection is the recommended practice
    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    // List all promotions
    @GetMapping
    public String listPromotions(Model model) {
        // CORRECTED: Use 'findAll'
        model.addAttribute("promotions", promotionService.findAll());
        return "promotions/list";
    }

    // Show create promotion form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        return "promotions/form";
    }

    // Create new promotion
    @PostMapping
    public String createPromotion(@Valid @ModelAttribute Promotion promotion,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "promotions/form";
        }

        // CORRECTED: Use 'save' for both create and update
        promotionService.save(promotion);
        // CORRECTED: Use 'successMessage' for SweetAlert consistency
        redirectAttributes.addFlashAttribute("successMessage", "Promotion created successfully!");
        return "redirect:/promotions";
    }

    // Show edit promotion form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // CORRECTED: Use functional map/orElseGet for Optionals
        return promotionService.findById(id)
                .map(promotion -> {
                    if ("SENT".equals(promotion.getStatus())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit sent promotions!");
                        return "redirect:/promotions";
                    }
                    model.addAttribute("promotion", promotion);
                    return "promotions/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Promotion not found!");
                    return "redirect:/promotions";
                });
    }

    // Update promotion
    @PostMapping("/update/{id}")
    public String updatePromotion(@PathVariable Long id,
                                  @Valid @ModelAttribute Promotion promotion,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Redirect back to edit form on validation error
            redirectAttributes.addFlashAttribute("errorMessage", "Validation errors occurred. Please check the form.");
            return "redirect:/promotions/edit/" + id;
        }

        try {
            // Ensure the ID is set for the update
            promotion.setId(id);
            // CORRECTED: Use 'save' for update
            promotionService.save(promotion);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/promotions";
    }

    // Delete promotion
    @GetMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // CORRECTED: Use 'deleteById'
            promotionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/promotions";
    }

    // Show send promotion form
    @GetMapping("/send/{id}")
    public String showSendForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // CORRECTED: Use 'findById'
        return promotionService.findById(id)
                .map(promotion -> {
                    if ("SENT".equals(promotion.getStatus())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Promotion has already been sent!");
                        return "redirect:/promotions";
                    }
                    model.addAttribute("promotion", promotion);
                    return "promotions/send";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Promotion not found!");
                    return "redirect:/promotions";
                });
    }

    // Send promotion
    @PostMapping("/send/{id}")
    public String sendPromotion(@PathVariable Long id,
                                @RequestParam String recipientEmails, // Keeping original parameter name
                                RedirectAttributes redirectAttributes) {
        try {
            // 1. Parse and trim comma-separated emails
            List<String> emails = Arrays.stream(recipientEmails.split(","))
                    .map(String::trim)
                    .filter(email -> !email.isEmpty())
                    .collect(Collectors.toList());

            if (emails.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No valid recipient emails were provided.");
                return "redirect:/promotions/send/" + id;
            }

            // 2. Retrieve the Promotion object
            Promotion promotion = promotionService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));

            int successCount = 0;
            // 3. CORRECTED LOGIC: Iterate and call the single-recipient service method
            for (String email : emails) {
                promotionService.sendPromotion(promotion, email);
                successCount++;
            }

            // 4. Set Success Message
            redirectAttributes.addFlashAttribute("successMessage",
                    "Promotion sent successfully to " + successCount + " recipient(s)!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email sending failed: " + e.getMessage());
        }
        return "redirect:/promotions";
    }

    // NEW: View promotion details (works for sent promotions too!)
    @GetMapping("/view/{id}")
    public String viewPromotion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // CORRECTED: Use 'findById'
        return promotionService.findById(id)
                .map(promotion -> {
                    model.addAttribute("promotion", promotion);
                    return "promotions/view";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Promotion not found!");
                    return "redirect:/promotions";
                });
    }
}