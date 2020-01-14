package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BlockchainService {

    private PeerGroup peerGroup;
    private final WalletService walletService;

    public BlockchainService(WalletService walletService){
        this.walletService = walletService;
        createBlockchainFile();
    }

    public void createBlockchainFile() {
        try {
            BlockStore blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File("blockchain"));
            BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), walletService.getAllWallets(), blockStore);
            peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
            peerGroup.setUserAgent("cryptowalletservice", "0.1");
            peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
            peerGroup.start();
            updateLocalBlockchain();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 90_000)
    private void updateLocalBlockchain() {
        System.out.println("Refreshing blockchain");

        peerGroup.downloadBlockChain();
        System.out.println("Blockchain refreshed");
    }

    public PeerGroup getPeerGroup(){
        return peerGroup;
    }
}
