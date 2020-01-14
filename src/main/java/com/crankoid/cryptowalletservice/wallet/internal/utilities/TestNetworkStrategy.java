package com.crankoid.cryptowalletservice.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class TestNetworkStrategy implements NetworkStrategy {

    @Override
    public NetworkParameters getNetwork() {
        return TestNet3Params.get();
    }
}
