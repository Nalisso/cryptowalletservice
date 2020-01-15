package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlockchainService {

    private final WalletService walletService;
    private BlockStore blockStore;

    public BlockchainService(WalletService walletService) {
        this.walletService = walletService;
    }
/*
    //Only refresh as new blocks are mined, roughly every 10 minutes
    @Scheduled(fixedRate = 600_000, initialDelay = 1_000)
    public void replayBlockchain() {
        Map<String, Wallet> storedWallets = walletService.getAllWallets();
        storedWallets.forEach((userId, wallet) -> System.out.println(String.format("Wallet owned by %s, will be replayed", userId)));
        replayBlockchain(storedWallets);
    }
    */
    /*

    private void replayBlockchain(Map<String, Wallet> wallets) {
        System.out.println("Replaying blockchain with all wallets");
        try {
            if (blockStore != null) {
                blockStore.close();
            }
            blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File("local_blockchain"));
            BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), new ArrayList<>(wallets.values()), blockStore);
            PeerGroup peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
            peerGroup.setUserAgent("cryptowalletservice", "0.1");
            peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
            addAllWallets(peerGroup, new ArrayList<>(wallets.values()));
            peerGroup.start();
            peerGroup.downloadBlockChain();
            peerGroup.stop();
            saveAllWallets(wallets);
            System.out.println("Blockchain replayed");
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }
    */


    public void replayBlockchain(Wallet wallet, String userId){
            try {
                System.out.println("Replaying blockchain");
                if (blockStore != null) {
                    blockStore.close();
                }
                blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File("local_blockchain"));
                BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), wallet, blockStore);
                PeerGroup peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
                peerGroup.setUserAgent("cryptowalletservice", "0.1");
                peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
                addAllWallets(peerGroup, wallet);
                peerGroup.start();
                peerGroup.downloadBlockChain();
                peerGroup.stop();
                PersonalWallet.save(userId, wallet);
                System.out.println("Blockchain replayed");
            } catch (BlockStoreException e) {
                e.printStackTrace();
            }
        }
    private void addAllWallets(PeerGroup peerGroup, Wallet wallet) {

            peerGroup.removeWallet(wallet);
            peerGroup.addWallet(wallet);

    }

    private void saveAllWallets(Map<String, Wallet> wallets) {
        wallets.forEach(PersonalWallet::save);
    }
}
