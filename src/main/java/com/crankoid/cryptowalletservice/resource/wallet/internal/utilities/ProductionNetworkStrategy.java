package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

class ProductionNetworkStrategy implements NetworkStrategy {

    @Override
    public NetworkParameters getNetwork() {
        return MainNetParams.get();
    }
}
