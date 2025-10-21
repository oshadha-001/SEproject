// StaffController.java
package com.booknest.booknest.controller;

import com.booknest.booknest.model.Staff;
import com.booknest.booknest.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staff/list";
    }

    @GetMapping("/new")
    public String createStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/form";
    }

    @PostMapping
    public String saveStaff(@Valid @ModelAttribute Staff staff, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "staff/form";
        }
        staffService.saveStaff(staff);
        return "redirect:/staff?notice=" + java.net.URLEncoder.encode("Staff saved successfully", java.nio.charset.StandardCharsets.UTF_8) + "&type=success";
    }

    @GetMapping("/edit/{id}")
    public String editStaffForm(@PathVariable Long id, Model model) {
        Staff staff = staffService.getStaffById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid staff Id: " + id));
        model.addAttribute("staff", staff);
        return "staff/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return "redirect:/staff?notice=" + java.net.URLEncoder.encode("Staff deleted", java.nio.charset.StandardCharsets.UTF_8) + "&type=warning";
    }
}