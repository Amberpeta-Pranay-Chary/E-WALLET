package com.example.repositories;

import com.example.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    @Query("select e from Wallet e where e.walletId=?1")
    Wallet findByWalletId(String walletId);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance=w.balance + :amount where w.walletId=:walletId")
    void updateWallet(String walletId,Long amount);
}
