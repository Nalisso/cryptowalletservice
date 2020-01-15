package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.core.BlockChain;
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

@Service
public class BlockchainService {

    private BlockStore blockStore;

    public BlockchainService() {
    }

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 5 * 60 * 1000)
    public void updateLocalBlockchain() {
        /*
        try {
            System.out.println("Refreshing blockchain");
            BlockStore blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File("local_blockchain"));
            BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), walletService.getAllWallets(), blockStore);
            PeerGroup peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
            peerGroup.setUserAgent("cryptowalletservice", "0.1");
            peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
            peerGroup.start();
            peerGroup.downloadBlockChain();
            peerGroup.stop();
            //walletService.getAllWallets().get(0).saveToFile(new File("BitcoinWallet-mada42"));
            //System.out.println(walletService.getAllWallets().get(0).toString());
            System.out.println("Blockchain refreshed");
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
        */
    }

    public void replayBlockchain(Wallet wallet, String filename) {
        try {
            System.out.println("Replaying blockchain");
            if (blockStore != null){
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
            wallet.saveToFile(new File(filename));
            System.out.println("Blockchain replayed");
        } catch (BlockStoreException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addAllWallets(PeerGroup peerGroup, Wallet wallet){
        peerGroup.addWallet(wallet);
    }
}
