package com.payflow.payflow.service;

import com.payflow.payflow.dto.request.MerchantLoginRequest;
import com.payflow.payflow.dto.request.MerchantSignupRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.MerchantResponse;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.repository.MerchantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public MerchantService(MerchantRepository merchantRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ApiResponse<List<MerchantResponse>> getAllMerchants() {

        List<MerchantResponse> merchants = merchantRepository.findAll()
                .stream()
                .map(this::toMerchantResponse)
                .toList();

        return ApiResponse.success("Merchants fetched successfully", merchants);
    }

    public ApiResponse<MerchantResponse> signup(MerchantSignupRequest request) {

        if (merchantRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Merchant already exists");
        }

        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));

        Merchant savedMerchant = merchantRepository.save(merchant);

        return ApiResponse.success(
                "Merchant registered successfully",
                toMerchantResponse(savedMerchant)
        );
    }

    public ApiResponse<String> login(MerchantLoginRequest request) {

        Merchant merchant = merchantRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        if (!passwordEncoder.matches(request.getPassword(), merchant.getPassword())) {
            return ApiResponse.error("Invalid credentials");
        }

        String token = jwtService.generateToken(merchant.getEmail());

        return ApiResponse.success("Login successful", token);
    }

    public ApiResponse<Void> deleteMerchant(Long id) {

        if (!merchantRepository.existsById(id)) {
            return ApiResponse.error("Merchant not found");
        }

        merchantRepository.deleteById(id);

        return ApiResponse.success("Merchant deleted successfully", null);
    }

    public Merchant getLoggedInMerchant() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return merchantRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
    }

    private MerchantResponse toMerchantResponse(Merchant merchant) {
        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getEmail()
        );
    }
}
