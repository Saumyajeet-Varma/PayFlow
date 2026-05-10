package com.payflow.payflow.service;

import com.payflow.payflow.core.dto.MerchantSignupRequest;
import com.payflow.payflow.core.entity.Merchant;
import com.payflow.payflow.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public String signup(MerchantSignupRequest request) {

        if (merchantRepository.existsByEmail(request.getEmail())) {
            return "Merchant already exists";
        }

        Merchant merchant = new Merchant();

        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());

        merchantRepository.save(merchant);

        return "Merchant Registered Successfully";
    }

    public String deleteMerchant(Long id) {

        if (!merchantRepository.existsById(id)) {
            return "Merchant Not Found";
        }

        merchantRepository.deleteById(id);

        return "Merchant Deleted Successfully";
    }
}