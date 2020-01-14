package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BlockchainService {

    private BlockChain blockchain;
    private PeerGroup peerGroup;

    public BlockchainService(){
        createBlockchainFile();
    }

    public void createBlockchainFile() {
        try {
            BlockStore blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File(new ClassPathResource("local_blockchain").getPath()));
            blockchain = new BlockChain(BitcoinNetwork.get(), blockStore);
            updateLocalBlockchain();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 30 * 1000, initialDelay = 90 * 1000)
    private void updateLocalBlockchain() {
        peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
        peerGroup.setUserAgent("cryptowalletservice", "0.1");
        peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
        peerGroup.start();
        peerGroup.downloadBlockChain();
        System.out.println("Refreshing blockchain");
    }

    public PeerGroup getPeerGroup(){
        return peerGroup;
    }
}
