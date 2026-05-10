package com.payflow.payflow.core.dto.request;

public class MerchantSignupRequest {

    private String name;
    private String email;

    public MerchantSignupRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}