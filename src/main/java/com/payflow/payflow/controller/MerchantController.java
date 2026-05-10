package com.payflow.payflow.controller;

import com.payflow.payflow.dto.request.MerchantSignupRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.MerchantResponse;
import com.payflow.payflow.service.MerchantService;
import jakarta.validation.Valid;
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
    public ApiResponse<List<MerchantResponse>> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteMerchant(@PathVariable Long id) {
        return merchantService.deleteMerchant(id);
    }

    @PostMapping("/signup")
    public ApiResponse<MerchantResponse> signup(@Valid @RequestBody MerchantSignupRequest request) {
        return merchantService.signup(request);
    }
}