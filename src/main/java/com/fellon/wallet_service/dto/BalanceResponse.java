package com.fellon.wallet_service.dto;

import java.util.UUID;
//ответ
public record BalanceResponse(
        UUID walletId,
        long balanceMinor,
        String currency
) {}