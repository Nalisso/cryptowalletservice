package com.crankoid.cryptowalletservice.service.blockchain;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BlockchainService {

    private BlockStore blockStore;

    public BlockchainService() {
    }

    public Wallet replayBlockchain(Wallet wallet, String userId) {
        try {
            System.out.println(String.format("Replaying blockchain for %s", userId));
            if (blockStore != null) {
                blockStore.close();
            }
            blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File(String.format("Blockchain-%s", userId)));
            BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), wallet, blockStore);
            PeerGroup peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
            peerGroup.setUserAgent("BitcoinWalletService", "0.1");
            peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
            peerGroup.addWallet(wallet);
            peerGroup.start();
            peerGroup.downloadBlockChain();
            peerGroup.stop();
            PersonalWallet.save(userId, wallet);
            System.out.println(String.format("Blockchain replayed for %s", userId));
            return wallet;
        } catch (BlockStoreException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public PeerGroup getPaymentPeerGroup(Wallet senderWallet, String senderUserId) {
        try {
            if (blockStore != null) {
                blockStore.close();
            }
            blockStore = new SPVBlockStore(BitcoinNetwork.get(), new File(String.format("Blockchain-%s", senderUserId)));
            BlockChain blockchain = new BlockChain(BitcoinNetwork.get(), senderWallet, blockStore);
            PeerGroup peerGroup = new PeerGroup(BitcoinNetwork.get(), blockchain);
            peerGroup.setUserAgent("BitcoinWalletService", "0.1");
            peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.get()));
            peerGroup.addWallet(senderWallet);
            return peerGroup;
        } catch (BlockStoreException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
