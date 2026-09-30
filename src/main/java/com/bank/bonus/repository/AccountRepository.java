package com.bank.bonus.repository;

import com.bank.bonus.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<CustomerAccount, Long> {

    /**
     * Клиент в системе один, но искать по «единственной записи» нельзя:
     * при добавлении второго клиента запрос молча вернёт не тот счёт.
     * Поэтому ищем явно — по минимальному id.
     */
    Optional<CustomerAccount> findFirstByOrderByIdAsc();
}
