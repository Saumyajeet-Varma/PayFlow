package com.payflow.payflow.controller;

import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.repository.MerchantRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantRepository merchantRepository;

    public MerchantController(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @GetMapping("/all")
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteMerchant(@PathVariable Long id) {
        merchantRepository.deleteById(id);
        return "Merchant Deleted Successfully";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody Merchant merchant) {
        if (merchantRepository.existsByEmail(merchant.getEmail())) {
            return "Merchant already exists";
        }
        merchantRepository.save(merchant);
        return "Merchant Registered Successfully";
    }
}