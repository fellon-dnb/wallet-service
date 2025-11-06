package com.fellon.wallet_service.controller;

import com.fellon.wallet_service.dto.BalanceResponse;
import com.fellon.wallet_service.dto.WalletOperationRequest;
import com.fellon.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @PostMapping("/wallet")
    public ResponseEntity<BalanceResponse> operate(@Valid @RequestBody WalletOperationRequest req) {
        return ResponseEntity.ok(service.applyOperation(req));
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(service.getBalance(walletId));
    }
}
