package org.simplebonus.controller;

import lombok.RequiredArgsConstructor;
import org.simplebonus.model.entity.PaymentPlace;
import org.simplebonus.service.SimpleBonusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер оплаты покупки.
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final SimpleBonusService simpleBonusService;

    // В условии задачи метод GET что априори не подходит для операции
    @PostMapping("/{place:Shop|Online}/{amount}")
    public ResponseEntity<String> getBonusesForPayment(@PathVariable PaymentPlace place, @PathVariable String amount) {
        return ResponseEntity.ok(simpleBonusService.processPayment(place, amount));
    }
}
