package com.bank.bonus.controller;

import com.bank.bonus.exception.InvalidAmountException;
import com.bank.bonus.exception.UnknownChannelException;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * HTTP-слой. Отвечает только за разбор строки запроса и вызов сервиса —
 * никаких вычислений бонусов здесь быть не должно.
 */
@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * {@code GET /api/payment/{Shop|Online}/{amount}} — 200 без тела.
     *
     * <p>Значение приходит строкой, поэтому разбор — здесь: контроллер отвечает за
     * то, что «abc» это не число, а сервис — за то, что отрицательная сумма
     * недопустима.
     */
    @GetMapping("/payment/{channel}/{amount}")
    public ResponseEntity<Void> pay(@PathVariable String channel, @PathVariable String amount) {
        paymentService.purchase(parseChannel(channel), parseAmount(amount));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/money")
    public BigDecimal money() {
        return paymentService.moneyBalance();
    }

    @GetMapping("/bankAccountOfEMoney")
    public BigDecimal bonusBalance() {
        return paymentService.bonusBalance();
    }

    @GetMapping("/commission")
    public BigDecimal commission() {
        return paymentService.bankCommission();
    }

    private PurchaseChannel parseChannel(String raw) {
        PurchaseChannel channel = PurchaseChannel.from(raw);
        if (channel == null) {
            throw new UnknownChannelException("Неизвестное место покупки: '%s'. Ожидается Shop или Online".formatted(raw));
        }
        return channel;
    }

    private BigDecimal parseAmount(String raw) {
        try {
            // setScale важен: без него "100.123" прошло бы дальше и тихо потеряло копейки
            // где-то в середине расчёта, а не на входе, где об этом честнее сказать.
            return new BigDecimal(raw.trim()).setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidAmountException("Некорректный формат суммы: '%s'".formatted(raw));
        }
    }
}
