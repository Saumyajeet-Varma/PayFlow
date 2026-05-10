package com.payflow.payflow.service;

import com.payflow.payflow.dto.request.MerchantSignupRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.MerchantResponse;
import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
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
}