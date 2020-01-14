package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoInsecureDTO;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.NetworkStrategy;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.TestNetworkStrategy;
import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.math.BigInteger;

@RestController()
public class WalletResourceImpl implements WalletResource {

    private ECKey ecKey = null;
    private final NetworkStrategy networkStrategy = new TestNetworkStrategy();

    private Wallet walletSend = createNewWallet();
    private Wallet walletReceive = createNewWallet();
    private PeerGroup peerGroup;

    @Override
    public WalletInfoInsecureDTO generateWallet(String userId) {
        WalletInfoInsecureDTO walletInfoInsecureDTO = new WalletInfoInsecureDTO();
        ecKey = new ECKey();
        walletInfoInsecureDTO.setPrivateKey(ecKey.getPrivateKeyAsHex());
        return walletInfoInsecureDTO;
    }
    
    private void generateWallet123(String userId){
        Wallet wallet = createNewWallet();
        //spara userId - wallet
    }

    @Override
    public String getWalletInformation(String userId) {
        try {
            BlockStore blockStore = new SPVBlockStore(networkStrategy.getNetwork(), new File(new ClassPathResource("local_blockchain").getPath()));
            BlockChain blockchain = new BlockChain(networkStrategy.getNetwork(), walletSend, blockStore);
            peerGroup = new PeerGroup(networkStrategy.getNetwork(), blockchain);
            peerGroup.setUserAgent("test", "1.0");
            peerGroup.addPeerDiscovery(new DnsDiscovery(networkStrategy.getNetwork()));
            peerGroup.addWallet(walletSend);
            peerGroup.addWallet(walletReceive);
            peerGroup.start();
            peerGroup.downloadBlockChain();
            System.out.println("Vi har en blockkedja!");
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
        return walletSend.toString() + "\n\n\n\n" + walletReceive.toString();
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, BigInteger satoshiAmount) {
        try {
            Address targetAddress = walletReceive.currentReceiveAddress();
            Wallet.SendResult result = walletSend.sendCoins(peerGroup, targetAddress, Coin.MILLICOIN);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "success";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }

    private Wallet createNewWallet() {
        return Wallet.createDeterministic(networkStrategy.getNetwork(), Script.ScriptType.P2PKH);
    }
}
