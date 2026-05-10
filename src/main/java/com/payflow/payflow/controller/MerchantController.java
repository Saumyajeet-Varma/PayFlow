package com.payflow.payflow.controller;

import com.payflow.payflow.core.dto.MerchantSignupRequest;
import com.payflow.payflow.core.entity.Merchant;
import com.payflow.payflow.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/all")
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteMerchant(@PathVariable Long id) {
        return merchantService.deleteMerchant(id);
    }

    @PostMapping("/signup")
    public String signup(@RequestBody MerchantSignupRequest request) {
        return merchantService.signup(request);
    }
}