package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.NetworkStrategy;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.TestNetworkStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.math.BigInteger;

@RestController()
public class WalletResourceImpl implements WalletResource {


    @Value("${spring.datasource.url}")
    private String dbUrl;

    private ECKey ecKey = null;
    private final NetworkStrategy networkStrategy = new TestNetworkStrategy();
    private Wallet walletSend = createNewWallet();
    private Wallet walletReceive = createNewWallet();
    private PeerGroup peerGroup;
    JdbcTemplate jdbcTemplate;

    static ObjectMapper mapper = new ObjectMapper();

    public WalletResourceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public String initBlockchain() {
        return null;
    }

    @Override
    public String generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        try {
            jdbcTemplate.update("INSERT INTO wallet (refid, keyValue) VALUES(?,?)", userId.getUserId(), mapper.writeValueAsString(createNewWallet().getKeyChainSeed()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return "OK";
    }

    private void generateWallet123(String userId) {
        Wallet wallet = createNewWallet();
        //spara userId - wallet
    }

    @Override
    public String getWalletInformation(UserId userId) {
        /*
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
         */
        return null;
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, BigInteger satoshiAmount) {
        try {
            Address targetAddress = walletReceive.currentReceiveAddress();
            Wallet.SendResult result = walletSend.sendCoins(peerGroup, targetAddress, Coin.MILLICOIN);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "OK";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }

    private Wallet createNewWallet() {
        return Wallet.createDeterministic(networkStrategy.getNetwork(), Script.ScriptType.P2PKH);
    }
}
