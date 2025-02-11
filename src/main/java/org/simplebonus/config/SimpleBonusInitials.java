package org.simplebonus.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@NoArgsConstructor
public final class SimpleBonusInitials {
    @Value("${initials.client-money}")
    private BigDecimal clientInitialMoney;

    @Value("${initials.threshold.enhanced-bonus}")
    private BigDecimal enhancedBonusThreshold;
    @Value("${initials.threshold.bank-commission}")
    private BigDecimal bankCommissionThreshold;

    @Value("${initials.bonus.bank-commission}")
    private BigDecimal bankCommissionPercent;
    @Value("${initials.bonus.shop}")
    private BigDecimal bonusRegularShop;
    @Value("${initials.bonus.online}")
    private BigDecimal bonusRegularOnline;
    @Value("${initials.bonus.enhanced-bonus}")
    private BigDecimal bonusEnhanced;

    public BigDecimal getClientInitialMoney() {
        return clientInitialMoney;
    }

    public BigDecimal getEnhancedBonusThreshold() {
        return enhancedBonusThreshold;
    }

    public BigDecimal getBankCommissionThreshold() {
        return bankCommissionThreshold;
    }

    public BigDecimal getBankCommissionPercent() {
        return bankCommissionPercent;
    }

    public BigDecimal getBonusRegularShop() {
        return bonusRegularShop;
    }

    public BigDecimal getBonusRegularOnline() {
        return bonusRegularOnline;
    }

    public BigDecimal getBonusEnhanced() {
        return bonusEnhanced;
    }
}
