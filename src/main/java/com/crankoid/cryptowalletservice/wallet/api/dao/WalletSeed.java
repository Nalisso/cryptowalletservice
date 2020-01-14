package com.crankoid.cryptowalletservice.wallet.api.dao;

import java.util.List;

public class WalletSeed {

    List<String> mnemonicCode;
    byte[] seed;
    long creationTimeSeconds;

    public WalletSeed(){}

    public WalletSeed(
            List<String> mnemonicCode,
            byte[] seed,
            long creationTimeSeconds) {
        this.mnemonicCode = mnemonicCode;
        this.seed = seed;
        this.creationTimeSeconds = creationTimeSeconds;
    }

    public List<String> getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(List<String> mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public byte[] getSeed() {
        return seed;
    }

    public void setSeed(byte[] seed) {
        this.seed = seed;
    }

    public long getCreationTimeSeconds() {
        return creationTimeSeconds;
    }

    public void setCreationTimeSeconds(long creationTimeSeconds) {
        this.creationTimeSeconds = creationTimeSeconds;
    }

}
