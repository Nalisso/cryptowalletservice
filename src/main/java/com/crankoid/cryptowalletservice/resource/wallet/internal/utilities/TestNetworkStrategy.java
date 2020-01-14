package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

class TestNetworkStrategy implements NetworkStrategy {

    @Override
    public NetworkParameters getNetwork() {
        return TestNet3Params.get();
    }
}
