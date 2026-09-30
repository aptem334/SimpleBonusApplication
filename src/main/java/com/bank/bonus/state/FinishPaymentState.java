package com.bank.bonus.state;

import com.bank.bonus.exception.InsufficientFundsException;
import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.Money;
import com.bank.bonus.model.PurchaseTransaction;
import com.bank.bonus.model.TransactionType;
import com.bank.bonus.processor.PaymentContext;
import com.bank.bonus.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Финальное состояние: единственное, которое пишет в БД и меняет счёт.
 *
 * <p>Проверка достаточности средств живёт здесь, а не в {@link InitPaymentState},
 * сознательно: к этому моменту уже известна комиссия, и суммарное списание
 * равно {@code amount + commission}. Если бы проверка стояла в начале цепочки,
 * правило «< 20 → комиссия» пришлось бы продублировать в двух местах, и эти
 * места рано или поздно разошлись бы.
 *
 * <p>Если средств не хватило — бросаем исключение. Транзакция БД откатится,
 * поэтому частично применённого платежа не останется.
 */
@Component
public class FinishPaymentState implements PaymentState {

    private static final Logger log = LoggerFactory.getLogger(FinishPaymentState.class);

    private final TransactionRepository transactionRepository;

    public FinishPaymentState(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        CustomerAccount account = context.account();
        BigDecimal debit = context.totalDebit();

        if (account.getMoney().compareTo(debit) < 0) {
            throw new InsufficientFundsException(
                    "Недостаточно средств: нужно %s, доступно %s".formatted(debit, account.getMoney()));
        }

        account.setMoney(Money.round(account.getMoney().subtract(debit)));
        account.setBonus(Money.round(account.getBonus().add(context.bonus())));
        account.setBankCommission(Money.round(account.getBankCommission().add(context.commission())));

        transactionRepository.save(new PurchaseTransaction(
                TransactionType.PURCHASE,
                context.channel(),
                Money.round(context.amount()),
                context.bonus(),
                context.commission(),
                account.getMoney(),
                account.getBonus()));

        log.info("Платёж {} на {} завершён: бонус={}, комиссия={}, остаток={}",
                context.channel(), context.amount(), context.bonus(), context.commission(), account.getMoney());

        return null;
    }
}
