package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.NetworkStrategy;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.TestNetworkStrategy;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BlockchainService {

    private BlockStore blockStore;
    private BlockChain blockchain;
    private PeerGroup peerGroup;
    private final NetworkStrategy networkStrategy = new TestNetworkStrategy();

    public BlockchainService(){
        updateBlockchainFile();
    }

    public void updateBlockchainFile() {
        try {
            blockStore = new SPVBlockStore(networkStrategy.getNetwork(), new File(new ClassPathResource("local_blockchain").getPath()));
            blockchain = new BlockChain(networkStrategy.getNetwork(), blockStore);
            updateLocalBlockchain();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }

    private void updateLocalBlockchain() {
        peerGroup = new PeerGroup(networkStrategy.getNetwork(), blockchain);
        peerGroup.setUserAgent("cryptowalletservice", "0.1");
        peerGroup.addPeerDiscovery(new DnsDiscovery(networkStrategy.getNetwork()));
        peerGroup.start();
        peerGroup.downloadBlockChain();
    }

    public PeerGroup getPeerGroup(){
        return peerGroup;
    }
}
