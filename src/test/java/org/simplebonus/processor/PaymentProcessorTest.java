package org.simplebonus.processor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.model.entity.PaymentPlace;
import org.simplebonus.processor.PaymentProcessor;
import org.simplebonus.repository.PaymentHistoryRepository;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentProcessorTest {

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Mock
    private SimpleBonusInitials simpleBonusInitials;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    private BigDecimal initialAmount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initialAmount = BigDecimal.valueOf(5000);

        when(simpleBonusInitials.getClientInitialMoney()).thenReturn(initialAmount);
        when(simpleBonusInitials.getBonusRegularOnline()).thenReturn(BigDecimal.valueOf(17));
        when(simpleBonusInitials.getBonusRegularShop()).thenReturn(BigDecimal.valueOf(10));
        when(simpleBonusInitials.getBonusEnhanced()).thenReturn(BigDecimal.valueOf(30));
        when(simpleBonusInitials.getBankCommissionPercent()).thenReturn(BigDecimal.valueOf(10));
        when(simpleBonusInitials.getEnhancedBonusThreshold()).thenReturn(BigDecimal.valueOf(300));
        when(simpleBonusInitials.getBankCommissionThreshold()).thenReturn(BigDecimal.valueOf(20));
    }

    @Test
    void testPaymentOnline100() {
        BigDecimal amount = BigDecimal.valueOf(100);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.ONLINE, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.17));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }


    @Test
    void testPaymentOnline301() {
        BigDecimal amount = BigDecimal.valueOf(301);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.ONLINE, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.30));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }

    @Test
    void testPaymentOnline17() {
        BigDecimal amount = BigDecimal.valueOf(17);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.ONLINE, amount);
        assertNotNull(result);
        BigDecimal expectedCommission = amount.multiply(BigDecimal.valueOf(0.10));
        assertEquals(0, expectedCommission.compareTo(result.getBankCommission()));
    }

    @Test
    void testPaymentShop1000() {
        BigDecimal amount = BigDecimal.valueOf(1000);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.SHOP, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.30));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }

    @Test
    void testPaymentOnline21() {
        BigDecimal amount = BigDecimal.valueOf(21);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.ONLINE, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.17));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }

    @Test
    void testPaymentShop570() {
        BigDecimal amount = BigDecimal.valueOf(570);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.SHOP, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.30));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }

    @Test
    void testPaymentOnline700() {
        BigDecimal amount = BigDecimal.valueOf(700);
        var result = paymentProcessor.processPayment(simpleBonusInitials, PaymentPlace.ONLINE, amount);
        assertNotNull(result);
        BigDecimal expectedBonus = amount.multiply(BigDecimal.valueOf(0.30));
        assertEquals(0, expectedBonus.compareTo(result.getClientBonus()));
    }
}
