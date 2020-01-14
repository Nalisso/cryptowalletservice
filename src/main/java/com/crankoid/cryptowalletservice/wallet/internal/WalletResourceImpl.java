package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletDTO;
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
    private PeerGroup peerGroup;
    private BlockStore blockStore;
    private BlockChain blockchain;
    JdbcTemplate jdbcTemplate;

    static ObjectMapper mapper = new ObjectMapper();

    public WalletResourceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String updateBlockchainFile() {
        try {
            blockStore = new SPVBlockStore(networkStrategy.getNetwork(), new File(new ClassPathResource("local_blockchain").getPath()));
            blockchain = new BlockChain(networkStrategy.getNetwork(), blockStore);
            updateLocalBlockchain();
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    private void updateLocalBlockchain() {
        peerGroup = new PeerGroup(networkStrategy.getNetwork(), blockchain);
        peerGroup.setUserAgent("cryptowalletservice", "0.1");
        peerGroup.addPeerDiscovery(new DnsDiscovery(networkStrategy.getNetwork()));
        peerGroup.start();
        peerGroup.downloadBlockChain();
    }

    @Override
    public String generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        try {
            Wallet wallet = createNewWallet();
            DeterministicSeed seed = wallet.getKeyChainSeed();
            WalletSeed walletSeed = new WalletSeed(seed.getMnemonicCode(), seed.getSeedBytes(), seed.getCreationTimeSeconds());
            peerGroup.addWallet(wallet);
            jdbcTemplate.update(
                    "INSERT INTO wallet (refId, keyValue) VALUES(?,?)",
                    userId.getUserId(),
                    mapper.writeValueAsString(walletSeed));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return "OK";
    }

    @Override
    public WalletDTO getWallet(UserId userId) {
        return convertWallet(getWalletFromUserId(userId.getUserId()), userId.getUserId());
    }

    private Wallet getWalletFromUserId(String userId){
        String result = jdbcTemplate.queryForObject("SELECT keyValue FROM wallet WHERE refId = ?",
                new Object[]{userId}, String.class);
        try {
            WalletSeed walletSeed = mapper.readValue(result, WalletSeed.class);
            DeterministicSeed seed = new DeterministicSeed(
                    walletSeed.getSeed(),
                    walletSeed.getMnemonicCode(),
                    walletSeed.getCreationTimeSeconds());
            return Wallet.fromSeed(networkStrategy.getNetwork(), seed, Script.ScriptType.P2PKH);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private WalletDTO convertWallet(Wallet wallet, String userId) {
        WalletDTO walletDTO = new WalletDTO();
        BalanceDTO balanceDTO = new BalanceDTO();
        walletDTO.setAddress(wallet.currentReceiveAddress().toString());
        balanceDTO.setAvailable(wallet.getBalance(Wallet.BalanceType.AVAILABLE).value);
        balanceDTO.setEstimated(wallet.getBalance(Wallet.BalanceType.ESTIMATED).value);
        walletDTO.setBalance(balanceDTO);
        walletDTO.setUserId(userId);
        return walletDTO;
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, String satoshiAmount) {
        try {
            Wallet walletSend = getWalletFromUserId(sourceUserId);
            Wallet walletReceive = getWalletFromUserId(destinationUserId);
            Address targetAddress = walletReceive.currentReceiveAddress();
            Coin amount = Coin.parseCoin(satoshiAmount);
            Wallet.SendResult result = walletSend.sendCoins(peerGroup, targetAddress, amount);
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
