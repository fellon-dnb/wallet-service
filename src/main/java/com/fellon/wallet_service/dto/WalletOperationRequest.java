package com.fellon.wallet_service.dto;

import com.fellon.wallet_service.domain.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;
//запрос
public record WalletOperationRequest(
        @NotNull UUID walletId,
        @NotNull OperationType operationType,
        @Positive long amount // в минорных единицах; > 0
) {}
