package org.simplebonus.controller;

import lombok.RequiredArgsConstructor;
import org.simplebonus.service.SimpleBonusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Контроллер получения количества бонусов.
 */

@RestController
@RequestMapping("/api/bankAccountOfEMoney")
@RequiredArgsConstructor
public class BankEarnCommissionController {
    private final SimpleBonusService simpleBonusService;

    @GetMapping
    public ResponseEntity<BigDecimal> getCommissionTotal() {
        return ResponseEntity.ok(simpleBonusService.getBankCommissionTotal());
    }
}
