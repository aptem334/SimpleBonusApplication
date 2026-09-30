package com.bank.bonus.model;

/**
 * Место совершения покупки. Значение enum'а попадает в URL-путь
 * ({@code /api/payment/{Shop|Online}/{amount}}), поэтому сравнение в контроллере
 * должно быть регистронезависимым — см. {@link #from(String)}.
 */
public enum PurchaseChannel {

    SHOP,
    ONLINE;

    /**
     * Разбирает канал из URL. Регистр не важен: в ТЗ написано {@code Shop}, но
     * пользователь почти наверняка напишет {@code shop}.
     *
     * @return канал или {@code null}, если значение не распознано
     */
    public static PurchaseChannel from(String raw) {
        if (raw == null) {
            return null;
        }
        for (PurchaseChannel channel : values()) {
            if (channel.name().equalsIgnoreCase(raw.trim())) {
                return channel;
            }
        }
        return null;
    }

    /** Имя канала в том виде, в котором оно ожидается в URL. */
    public String urlValue() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
