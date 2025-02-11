package org.simplebonus.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.simplebonus.controller.PaymentController;
import org.simplebonus.model.entity.PaymentPlace;
import org.simplebonus.service.SimpleBonusService;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private SimpleBonusService simpleBonusService;

    public PaymentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPaymentShop() {
        when(simpleBonusService.processPayment(PaymentPlace.SHOP, "100"))
                .thenReturn("Оплата обработана успешно");

        ResponseEntity<String> response = paymentController.getBonusesForPayment(PaymentPlace.SHOP.name(), "100");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Оплата обработана успешно", response.getBody());
    }

    @Test
    void testPaymentOnline() {
        when(simpleBonusService.processPayment(PaymentPlace.ONLINE, "250"))
                .thenReturn("Оплата обработана успешно");

        ResponseEntity<String> response = paymentController.getBonusesForPayment(PaymentPlace.ONLINE.name(), "250");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Оплата обработана успешно", response.getBody());
    }

    @Test
    void testPaymentInvalidAmount() {
        when(simpleBonusService.processPayment(PaymentPlace.ONLINE, "invalid"))
                .thenThrow(new NumberFormatException("Некорректная сумма"));

        assertThrows(NumberFormatException.class, () -> {
            paymentController.getBonusesForPayment(PaymentPlace.ONLINE.name(), "invalid");
        });
    }
}