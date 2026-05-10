package com.payflow.payflow.controller;

import com.payflow.payflow.model.Merchant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @PostMapping("/signup")
    public String signup(@RequestBody Merchant merchant) {

        System.out.println(merchant.getName());
        System.out.println(merchant.getEmail());

        return "Registered Successfully";
    }
}
