package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;

public class PersonalWallet {

    private static final String walletPrefix = "BitcoinWallet-";

    public static void save(String userId, Wallet wallet){
        try {
            wallet.saveToFile(new File(walletPrefix.concat(userId)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save wallet, see stacktrace.", e);
        }
    }

    public static Wallet load(String userId){
        try {
            return Wallet.loadFromFile(new File(walletPrefix.concat(userId)), null);
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load wallet, see stacktrace.", e);
        }
    }

    public static boolean remove(String userId){
        return new File(walletPrefix.concat(userId)).delete();
    }

}
