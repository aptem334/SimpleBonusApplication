package org.simplebonus.controller;

import lombok.RequiredArgsConstructor;
import org.simplebonus.service.SimpleBonusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
/**
 * Контроллер получения бонусного баланса.
 */
@RestController
@RequestMapping("/api/bonusBalance")
@RequiredArgsConstructor
public class ClientBonusController {
    private final SimpleBonusService simpleBonusService;

    @GetMapping
    public ResponseEntity<BigDecimal> getClientBonusBalance() {
        return ResponseEntity.ok(simpleBonusService.getClientAccruedBonuses());
    }
}
