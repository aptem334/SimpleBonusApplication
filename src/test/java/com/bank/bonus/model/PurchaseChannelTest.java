package com.bank.bonus.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseChannelTest {

    @ParameterizedTest
    @CsvSource({"Shop", "shop", "SHOP", " sHoP "})
    @DisplayName("Канал распознаётся независимо от регистра и лишних пробелов")
    void parsesChannelIgnoringCase(String raw) {
        assertThat(PurchaseChannel.from(raw)).isEqualTo(PurchaseChannel.SHOP);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Market", "Shop1", "", "интернет"})
    @DisplayName("Неизвестный канал даёт null, чтобы вызывающий код сообщил 400")
    void returnsNullForUnknownChannel(String raw) {
        assertThat(PurchaseChannel.from(raw)).isNull();
    }

    @Test
    @DisplayName("null-канал не роняет разбор, а тоже даёт null")
    void returnsNullForNull() {
        assertThat(PurchaseChannel.from(null)).isNull();
    }

    @Test
    @DisplayName("Значение для URL — с заглавной буквы, как в ТЗ")
    void urlValueIsCapitalized() {
        assertThat(PurchaseChannel.SHOP.urlValue()).isEqualTo("Shop");
        assertThat(PurchaseChannel.ONLINE.urlValue()).isEqualTo("Online");
    }
}
