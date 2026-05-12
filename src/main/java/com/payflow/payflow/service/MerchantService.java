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

        List<Merchant> merchants = merchantRepository.findAll();

        List<MerchantResponse> response = merchants
                .stream()
                .map(merchant -> new MerchantResponse(
                        merchant.getId(),
                        merchant.getName(),
                        merchant.getEmail()
                ))
                .toList();

        return new ApiResponse<>(
                true,
                "Merchants fetched successfully",
                response
        );
    }

    public ApiResponse<MerchantResponse> signup(MerchantSignupRequest request) {

        if (merchantRepository.existsByEmail(request.getEmail())) {

            return new ApiResponse<>(
                    false,
                    "Merchant already exists",
                    null
            );
        }

        Merchant merchant = new Merchant();

        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        Merchant savedMerchant = merchantRepository.save(merchant);

        MerchantResponse response = new MerchantResponse(
                savedMerchant.getId(),
                savedMerchant.getName(),
                savedMerchant.getEmail()
        );

        return new ApiResponse<>(
                true,
                "Merchant Registered Successfully",
                response
        );
    }

    public ApiResponse<String> login(MerchantLoginRequest request) {

        Merchant merchant = merchantRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant Not Found"));

        boolean passwordMatches = passwordEncoder.matches(
                        request.getPassword(),
                        merchant.getPassword()
                );

        if (!passwordMatches) {

            return new ApiResponse<>(
                    false,
                    "Invalid Credentials",
                    null
            );
        }

        String token = jwtService.generateToken(merchant.getEmail());

        return new ApiResponse<>(
                true,
                "Login Successful",
                token
        );
    }

    public ApiResponse<Void> deleteMerchant(Long id) {

        if (!merchantRepository.existsById(id)) {

            return new ApiResponse<>(
                    false,
                    "Merchant Not Found",
                    null
            );
        }

        merchantRepository.deleteById(id);

        return new ApiResponse<>(
                true,
                "Merchant Deleted Successfully",
                null
        );
    }

    public Merchant getLoggedInMerchant() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return merchantRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant Not Found"));
    }
}