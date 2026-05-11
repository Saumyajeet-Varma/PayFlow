package com.payflow.payflow.repository;

import com.payflow.payflow.model.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    boolean existsByEmail(String email);

    Optional<Merchant> findByEmail(String email);
}