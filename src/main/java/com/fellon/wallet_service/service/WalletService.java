package com.fellon.wallet_service.service;

import com.fellon.wallet_service.domain.OperationType;
import com.fellon.wallet_service.dto.BalanceResponse;
import com.fellon.wallet_service.dto.WalletOperationRequest;
import com.fellon.wallet_service.repo.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository repo;

    public WalletService(WalletRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public BalanceResponse applyOperation(WalletOperationRequest req) {
        var wallet = repo.findByIdForUpdate(req.walletId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found: " + req.walletId()));

        long balance = wallet.balanceMinor();
        long amount = req.amount();

        if (req.operationType() == OperationType.DEPOSIT) {
            balance += amount;
        } else if (req.operationType() == OperationType.WITHDRAW) {
            if (balance < amount) {
                throw new IllegalStateException("Insufficient funds for wallet " + wallet.id());
            }
            balance -= amount;
        } else {
            throw new IllegalArgumentException("Unknown operation type");
        }

        repo.updateBalance(wallet.id(), balance);
        repo.insertOperation(wallet.id(), req.operationType().name(), amount);

        return new BalanceResponse(wallet.id(), balance, wallet.currency());
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID walletId) {
        var wallet = repo.findById(walletId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found: " + walletId));
        return new BalanceResponse(wallet.id(), wallet.balanceMinor(), wallet.currency());
    }
}
