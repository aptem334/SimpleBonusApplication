package org.simplebonus.repository;

import org.simplebonus.model.entity.SinglePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
/**
 * Репозиторий для сохранения истории платежей.
 */
@Repository
public interface PaymentHistoryRepository extends JpaRepository<SinglePaymentEntity, Long> {
    SinglePaymentEntity findFirstByOrderByIdDesc();

    @Query("SELECT SUM(spe.clientBonus) FROM SinglePaymentEntity spe")
    BigDecimal sumAccruedBonuses();

    @Query("SELECT SUM(spe.bankCommission) FROM SinglePaymentEntity spe")
    BigDecimal sumBankCommissions();
}
