package com.payflow.payflow.repository;

import com.payflow.payflow.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByEmail(String email);
}