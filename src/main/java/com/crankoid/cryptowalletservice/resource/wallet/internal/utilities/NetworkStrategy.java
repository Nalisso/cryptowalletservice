package com.crankoid.cryptowalletservice.resource.wallet.internal.utilities;

import org.bitcoinj.core.NetworkParameters;

public interface NetworkStrategy {

    public NetworkParameters getNetwork();
}
