package com.bank.bonus.service;

import com.bank.bonus.exception.AccountNotFoundException;
import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.processor.PaymentContext;
import com.bank.bonus.processor.PaymentProcessor;
import com.bank.bonus.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Точка входа в бизнес-логику: загружает счёт, отдаёт платёж процессору,
 * отдаёт балансы наружу.
 *
 * <p>Граница транзакции проходит именно здесь. Процессор сознательно не аннотирован
 * {@code @Transactional}: у него нет своего бизнес-смысла, и если бы он открывал
 * транзакцию сам, то вызов из другого места мог бы получить две вложенные транзакции
 * и потерять атомарность.
 */
@Service
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentProcessor paymentProcessor;

    public PaymentService(AccountRepository accountRepository, PaymentProcessor paymentProcessor) {
        this.accountRepository = accountRepository;
        this.paymentProcessor = paymentProcessor;
    }

    /**
     * Обрабатывает покупку целиком.
     *
     * <p>Если внутри выбросится {@code InsufficientFundsException}, транзакция
     * откатится — частично применённого платежа не останется. JPA отслеживает
     * изменённый {@code CustomerAccount}, поэтому явного {@code save} не требуется.
     */
    @Transactional
    public void purchase(PurchaseChannel channel, BigDecimal amount) {
        PaymentContext context = new PaymentContext(channel, amount, loadAccount());
        paymentProcessor.process(context);
    }

    @Transactional(readOnly = true)
    public BigDecimal moneyBalance() {
        return loadAccount().getMoney();
    }

    @Transactional(readOnly = true)
    public BigDecimal bonusBalance() {
        return loadAccount().getBonus();
    }

    @Transactional(readOnly = true)
    public BigDecimal bankCommission() {
        return loadAccount().getBankCommission();
    }

    private CustomerAccount loadAccount() {
        return accountRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AccountNotFoundException("Счёт клиента не найден"));
    }
}
