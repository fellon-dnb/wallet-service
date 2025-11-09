package com.fellon.wallet_service.repo;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepository {

    private final JdbcClient jdbc;

    public record
    WalletRow(UUID id, long balanceMinor, String currency) {}

    private static final RowMapper<WalletRow> MAPPER = (rs, rn) ->
            new WalletRow(
                    (UUID) rs.getObject("id"),
                    rs.getLong("balance_minor"),
                    rs.getString("currency")
            );

    public WalletRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Чтение кошелька с блокировкой на строку. */
    public Optional<WalletRow> findByIdForUpdate(UUID id) {
        return jdbc.sql("""
                SELECT id, balance_minor, currency
                FROM wallets
                WHERE id = :id
                FOR UPDATE
                """)
                .param("id", id)
                .query(MAPPER)
                .optional();
    }

    public Optional<WalletRow> findById(UUID id) {
        return jdbc.sql("""
                SELECT id, balance_minor, currency
                FROM wallets
                WHERE id = :id
                """)
                .param("id", id)
                .query(MAPPER)
                .optional();
    }

    public int updateBalance(UUID id, long newBalanceMinor) {
        return jdbc.sql("""
                UPDATE wallets
                SET balance_minor = :bal, updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
                """)
                .param("bal", newBalanceMinor)
                .param("id", id)
                .update();
    }

    public void insertOperation(UUID walletId, String type, long amountMinor) {
        jdbc.sql("""
                INSERT INTO operations(wallet_id, type, amount_minor)
                VALUES (:wid, :type, :amt)
                """)
                .param("wid", walletId)
                .param("type", type)
                .param("amt", amountMinor)
                .update();
    }
}
