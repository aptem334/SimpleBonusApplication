package com.bank.bonus.repository;

import com.bank.bonus.model.PurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<PurchaseTransaction, Long> {

    List<PurchaseTransaction> findAllByOrderByIdAsc();
}
