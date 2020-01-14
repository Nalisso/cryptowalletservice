package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoInsecureDTO;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.NetworkStrategy;
import com.crankoid.cryptowalletservice.wallet.internal.utilities.TestNetworkStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

@RestController()
public class WalletResourceImpl implements WalletResource {


    @Value("${spring.datasource.url}")
    private String dbUrl;

    private ECKey ecKey = null;
    private final NetworkStrategy networkStrategy = new TestNetworkStrategy();

    JdbcTemplate jdbcTemplate;

    static ObjectMapper mapper = new ObjectMapper();

    public WalletResourceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WalletInfoInsecureDTO generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) ||  userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }

        WalletInfoInsecureDTO walletInfoInsecureDTO = new WalletInfoInsecureDTO();
        ecKey = new ECKey();

        walletInfoInsecureDTO.setPrivateKey(ecKey.getPrivateKeyAsHex());
        walletInfoInsecureDTO.setWalletInfoDTO(getWalletInformation(userId.getUserId()));

        try {
            jdbcTemplate.update("INSERT INTO wallet (refid, keyValue) VALUES(?,?)", userId.getUserId(), mapper.writeValueAsString(walletInfoInsecureDTO));
        } catch (JsonProcessingException e) {
           throw new IllegalStateException(e);
        }

        return walletInfoInsecureDTO;
    }

    @Override
    public WalletInfoInsecureDTO getWallet(String userId) {

        String result = jdbcTemplate.queryForObject("SELECT keyValue FROM wallet WHERE refId = ?",
            new Object[]{userId}, String .class);

        try {
            return mapper.readValue(result, WalletInfoInsecureDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }


    }

    @Override
    public WalletInfoDTO getWalletInformation(String userId) {
        WalletInfoDTO walletInfoDTO = new WalletInfoDTO();
        ecKey = new ECKey();
        Address address = Address.fromKey(networkStrategy.getNetwork(), ecKey, Script.ScriptType.P2WPKH);
        walletInfoDTO.setPublicKey(ecKey.getPublicKeyAsHex());
        walletInfoDTO.setAddress(address.toString());
        walletInfoDTO.setAvailableSatoshis(0L);
        return walletInfoDTO;
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, BigInteger satoshiAmount) {

        try {
            KeyChainGroup keyChainGroup = KeyChainGroup.createBasic(networkStrategy.getNetwork());
            keyChainGroup.importKeys(ecKey);
            Wallet wallet = new Wallet(networkStrategy.getNetwork(), keyChainGroup);
            BlockStore blockStore = new SPVBlockStore(networkStrategy.getNetwork(), new File(new ClassPathResource("local_blockchain").getPath()));
            BlockChain blockchain = new BlockChain(networkStrategy.getNetwork(), wallet, blockStore);
            PeerGroup peerGroup = new PeerGroup(networkStrategy.getNetwork(), blockchain);
            peerGroup.setUserAgent("test", "1.0");
            peerGroup.addPeerDiscovery(new DnsDiscovery(networkStrategy.getNetwork()));
            peerGroup.addWallet(wallet);
            peerGroup.start();
            peerGroup.downloadBlockChain();
            System.out.println("Vi har en blockkedja!");
            Address targetAddress = Address.fromString(networkStrategy.getNetwork(), "1RbxbA1yP2Lebauuef3cBiBho853f7jxs");
            Wallet.SendResult result = wallet.sendCoins(peerGroup, targetAddress, Coin.SATOSHI);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "success";
        } catch (BlockStoreException | InsufficientMoneyException e) {
            e.printStackTrace();
            return "error";
        }
    }

}
