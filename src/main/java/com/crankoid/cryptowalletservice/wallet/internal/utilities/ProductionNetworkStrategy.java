package com.crankoid.cryptowalletservice.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

public class ProductionNetworkStrategy implements NetworkStrategy {

    @Override
    public NetworkParameters getNetwork() {
        return MainNetParams.get();
    }
}
